package leaf

import Probability
import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.*
import leaf.Line.Companion.getPositionByDistanceAndAngle
import leaf.Line.Companion.intersects
import node.Node
import node.Node.Companion.addNode
import node.NodeLink
import node.NodeLink.Companion.addNodeLink
import node.NodeLink.Companion.addNodeLinks
import node.NodeMesh

@ExperimentalUnsignedTypes
interface ILeaf {

    val uuid : UUID

    val description : String

    val topHeight : Int

    val height : Int

    val position : Point

    val distanceFromParent : Int

    val topAngle : Angle

    val angleFromParent : Angle

    val cumlAngleFromTop : Angle

    val parent : MutableList<ILeaf>

    fun parentEmpty() = parent.isNullOrEmpty()

    fun getParent() : ILeaf? = if ( parentEmpty() ) null else parent[0]

    val children : MutableList<ILeaf>

    fun childrenEmpty() = children.isNullOrEmpty()

    fun getChildrenList() : List<ILeaf>? = if ( childrenEmpty() ) null else children.toList()

    fun getChildrenSize(height: Int, topHeight : Int = height) : Int

    fun getVarianceChildAngle(variance : Angle) : Angle =
        this.angleFromParent + Angle.fromDegrees( Probability(0, variance.degrees.toInt()).getValue() )

    fun getConvergentChildAngle(variance : Angle, convergeToAngle : Angle = this.topAngle ) : Angle =
        ( convergeToAngle.times(2) + getVarianceChildAngle(variance).times(2) ) / 4

    fun getDivergentChildAngle(variance : Angle, divergeFromAngle : Angle = this.topAngle ) : Angle =
        ( ( Angle.fromDegrees(180) + divergeFromAngle).normalized.times(2) + getVarianceChildAngle(variance).times(2) ) / 4

    fun getList() : List<ILeaf> =
        if (childrenEmpty()) listOf(this)
        else listOf(this).plus(children.flatMap { child -> child.getList() } )

    fun getLineList() : List<Pair<Point, Point>?> =
        if (childrenEmpty()) listOf(null)
        else children.map {
            childLeaf -> Point(this.position.x, this.position.y) to Point(childLeaf.position.x, childLeaf.position.y)
        }.plus(children.flatMap {
            childLeaf -> childLeaf.getLineList()
        } ).filterNotNull()

//    fun getCurrentHeight() : Int =
//        if (childrenEmpty()) 0
//        else (1 + children.map { childLeaf -> childLeaf.getCurrentHeight() }.reduce { maxHeight : Int, childLeafHeight -> max(maxHeight, childLeafHeight) })

    companion object {

        val ViewPortXRangePx = 300
        val ViewPortYRangePx = 200
        val NextDistancePx = (ViewPortXRangePx / 100) * (ViewPortYRangePx / 100) * 10

        fun getNextDistancePxProb(): Int = ProbabilitySelect.psAccumulating(
            listOf(
                NextDistancePx,
                NextDistancePx / 2,
                NextDistancePx * 3 / 2,
                NextDistancePx * 3 / 4,
                NextDistancePx * 5 / 4,
                NextDistancePx * 1 / 4,
                NextDistancePx * 7 / 4
            )
        ).getSelectedProbability()!!

        fun getParentPosition(parent: MutableList<ILeaf>): Point =
            if (!parent.isNullOrEmpty()) parent[0].position else Point(256, 256)

        fun getChildPosition(parentPosition: Point, distanceFromParent: Int, childAngle: Angle): Point {

            return parentPosition.getPositionByDistanceAndAngle(distanceFromParent, childAngle)

        }

        fun List<ILeaf>.getList(): List<ILeaf> {
            val returnList = mutableListOf<ILeaf>()

            this.forEach {
                iLeaf -> iLeaf.getList().forEach {
                    child -> returnList.add(child)
                }
            }

            return returnList
        }

        fun List<ILeaf>.getLineList(): List<Pair<Point, Point>?> {
            val returnLineList = mutableListOf<Pair<Point, Point>?>()

            this.forEach {
                iLeaf -> iLeaf.getLineList().forEach {
                    line -> returnLineList.add(line)
                }
            }

            return returnLineList.filterNotNull()
        }

        fun ILeaf.add(child: ILeaf): ILeaf {

            this.children.add(child)

            child.parent.add(this)

            return this
        }

        fun List<ILeaf>.prune(): List<ILeaf> {

            val returnLeaves = this.toMutableList()

            val orderedLeaves = this.sortedBy { it.height }.sortedByDescending { abs(it.topAngle - it.cumlAngleFromTop) }

            orderedLeaves.forEach { outerLeaf ->
                orderedLeaves.forEach { innerLeaf ->
                    if ( (outerLeaf != innerLeaf) && (innerLeaf.height <= outerLeaf.height) ) {
                        val outerChildren = outerLeaf.getChildrenList()
                        val innerChildren = innerLeaf.getChildrenList()
                        if ( (outerChildren != null) && (innerChildren != null) ) {
                            outerChildren.forEach { outerChild ->
                                innerChildren.forEach { innerChild ->
                                    if (outerChild != innerLeaf) {
                                        if ( Pair(outerLeaf.position, outerChild.position).intersects(Pair(innerLeaf.position, innerChild.position)) ) {
//                                            println("intersecion at ${outerLeaf.position} to ${outerChild.position} and ${innerLeaf.position} to ${innerChild.position}")

                                            if ( returnLeaves.indexOf(innerChild.getParent()) != -1 ) {
                                                returnLeaves[returnLeaves.indexOf(innerChild.getParent())].children.remove(innerChild)
                                            }

                                            innerChild.getList().forEach{ returnLeaves.remove(it) }
                                        }
                                    }
                                }
                            }
                            //prune child angles <= 10 degrees
                            innerChildren.sortedBy { it.angleFromParent }.forEach { outerInnerChild ->
                                innerChildren.sortedBy { it.angleFromParent }.forEach { innerInnerChild ->
                                    if (outerInnerChild != innerInnerChild) {
                                        if ( abs(outerInnerChild.angleFromParent - innerInnerChild.angleFromParent) <= Angle.fromDegrees(10) )
                                            if ( (outerInnerChild.getList().size) > (innerInnerChild.getList().size) ) {
                                                if ( returnLeaves.indexOf(innerInnerChild.getParent()) != -1 ) {
                                                    returnLeaves[returnLeaves.indexOf(innerInnerChild.getParent())].children.remove(innerInnerChild)
                                                }

                                                innerInnerChild.getList().forEach{ returnLeaves.remove(it) }
                                            } else {
                                                if ( returnLeaves.indexOf(outerInnerChild.getParent()) != -1 ) {
                                                    returnLeaves[returnLeaves.indexOf(outerInnerChild.getParent())].children.remove(outerInnerChild)
                                                }

                                                outerInnerChild.getList().forEach{ returnLeaves.remove(it) }
                                            }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return returnLeaves.toList()
        }

        fun ILeaf.node(): Node {
            return Node(this.uuid, this.position)
        }

        fun ILeaf.nodeLinks(nodes: MutableList<Node>): MutableList<NodeLink> {
            val returnNodeLinks = mutableListOf<NodeLink>()

            if (!parentEmpty()) returnNodeLinks.addNodeLink(nodes, this.uuid, getParent()!!.uuid)

            if (!childrenEmpty()) this.getChildrenList()!!
                .forEach { childLeaf -> returnNodeLinks.addNodeLink(nodes, this.uuid, childLeaf.uuid) }

            return returnNodeLinks
        }

        fun ILeaf.nodes(): MutableList<Node> {
            val returnNodes = mutableListOf<Node>()

            returnNodes.addNode(this.node(), this.description)

            if (!parentEmpty()) returnNodes.addNode(getParent()!!.node(), this.description)

            if (!childrenEmpty()) this.getChildrenList()!!
                .forEach { child -> returnNodes.addNode(child.node(), this.description) }

//            println("leaf nodes: $returnNodes")

            return returnNodes
        }

        fun ILeaf.nodeMesh(): NodeMesh = NodeMesh(nodes = this.nodes(), nodeLinks = this.nodeLinks(this.nodes()))

        fun List<ILeaf>.nodes(): MutableList<Node> {
            val returnNodes = mutableListOf<Node>()

            this.forEach { iLeaf -> returnNodes.addNode(iLeaf.node(), iLeaf.description) }

            return returnNodes
        }

        fun List<ILeaf>.nodeLinks(nodes: MutableList<Node>): MutableList<NodeLink> {
            val returnNodeLinks = mutableListOf<NodeLink>()

            this.forEach { iLeaf -> returnNodeLinks.addNodeLinks(iLeaf.nodeLinks(nodes)) }

            return returnNodeLinks
        }

        fun List<ILeaf>.nodeMesh(): NodeMesh = NodeMesh(nodes = this.nodes(), nodeLinks = this.nodeLinks(this.nodes()))

        fun List<ILeaf>.nearestILeafOrderedAsc(refPosition : Point) : MutableList<ILeaf> {

            val iLeafDistMap = mutableMapOf<ILeaf, Double>()

            this.forEach { iLeaf ->
                val iLeafToRefDistance = iLeaf.position.distanceTo(refPosition)

                iLeafDistMap[iLeaf] = iLeafToRefDistance
            }

            return iLeafDistMap.toList().sortedBy { (_, dist) -> dist}.toMap().keys.toMutableList()
        }
    }
}