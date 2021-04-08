package leaf

import Probability
import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.*
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

    val parent : MutableList<ILeaf>

    fun parentEmpty() = parent.isNullOrEmpty()

    fun getParent() : ILeaf? = if ( parentEmpty() ) null else parent[0]

    val children : MutableList<ILeaf>

    fun childrenEmpty() = children.isNullOrEmpty()

    fun getChildrenList() : List<ILeaf>? = if ( childrenEmpty() ) null else children.toList()

    fun getChildrenSize(height: Int, topHeight : Int = height) : Int

    fun getVarianceChildAngle(variance : Angle) : Angle =
        this.angleFromParent + Angle.fromDegrees( Probability(0, variance.degrees.toInt()).getValue() )

    fun getConvergentChildAngle(variance : Angle, convergeToAngle : Angle = this.angleFromParent ) : Angle =
        ( convergeToAngle.times(2) + getVarianceChildAngle(variance).times(2) ) / 4

    fun getDivergentChildAngle(variance : Angle, divergeFromAngle : Angle = this.angleFromParent ) : Angle =
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

            val childX = when {
                (childAngle.degrees >= 0) && (childAngle.degrees < 90) -> parentPosition.x + distanceFromParent * cos(
                    childAngle
                )
                (childAngle.degrees >= 90) && (childAngle.degrees < 180) -> parentPosition.x - distanceFromParent * cos(
                    Angle.fromDegrees(180) - childAngle
                )
                (childAngle.degrees >= 180) && (childAngle.degrees < 270) -> parentPosition.x - distanceFromParent * cos(
                    childAngle - Angle.fromDegrees(180)
                )
                (childAngle.degrees >= 270) && (childAngle.degrees < 360) -> parentPosition.x + distanceFromParent * cos(
                    Angle.fromDegrees(360) - childAngle
                )
                else -> parentPosition.x + distanceFromParent * cos(childAngle) //360 degrees
            }

            val childY = when {
                (childAngle.degrees >= 0) && (childAngle.degrees < 90) -> parentPosition.y - distanceFromParent * sin(
                    childAngle
                )
                (childAngle.degrees >= 90) && (childAngle.degrees < 180) -> parentPosition.y - distanceFromParent * sin(
                    Angle.fromDegrees(180) - childAngle
                )
                (childAngle.degrees >= 180) && (childAngle.degrees < 270) -> parentPosition.y + distanceFromParent * sin(
                    childAngle - Angle.fromDegrees(180)
                )
                (childAngle.degrees >= 270) && (childAngle.degrees <= 360) -> parentPosition.y + distanceFromParent * sin(
                    Angle.fromDegrees(360) - childAngle
                )
                else -> parentPosition.y - distanceFromParent * sin(childAngle) //360 degrees
            }

            return Point(childX, childY)
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

        fun ILeaf.graft(child: ILeaf): ILeaf {

            this.children.add(child)
            child.parent.add(this)

            val xOffset = (this.position.x - child.position.x).toInt()
            val yOffset = (this.position.y - child.position.y).toInt()

            child.position.x += xOffset
            child.position.y += yOffset

            child.children.forEach {
                childSub -> childSub.move(xOffset, yOffset)
            }

            return this
        }

        fun ILeaf.move(xOffset: Int, yOffset: Int): ILeaf {

            this.position.x += xOffset
            this.position.y += yOffset

            if (!this.childrenEmpty()) this.children.forEach {
                    childSub -> childSub.move(xOffset, yOffset)
            }

            return this
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

            println("leaf nodes: $returnNodes")

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
    }
}