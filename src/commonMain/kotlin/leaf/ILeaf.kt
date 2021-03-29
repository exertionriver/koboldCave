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
import kotlin.math.max

@ExperimentalUnsignedTypes
interface ILeaf {

    val uuid : UUID

    val initHeight : Int

    val position : Point

    val distanceFromParent : Int

    val angleFromParent : Angle

    val parentLeaf : MutableList<ILeaf>

    fun parentEmpty() = parentLeaf.isNullOrEmpty()

    fun getParentLeaf() : ILeaf? = if ( parentEmpty() ) null else parentLeaf[0]

    val childrenLeaves : MutableList<ILeaf>

    fun childrenEmpty() = childrenLeaves.isNullOrEmpty()

    fun getChildrenLeavesList() : List<ILeaf>? = if ( childrenEmpty() ) null else childrenLeaves.toList()

    fun getChildAngle() : Angle =
        this.angleFromParent + Angle.fromDegrees(Probability(0, 30).getValue())

    fun getLeafList() : List<ILeaf> =
        if (childrenEmpty()) listOf(this)
        else listOf(this).plus(childrenLeaves.flatMap { childLeaf -> childLeaf.getLeafList() } )

    fun getLeafLineList() : List<Pair<Point, Point>?> =
        if (childrenEmpty()) listOf(null)
        else childrenLeaves.map { childLeaf -> Point(this.position.x, this.position.y) to Point(childLeaf.position.x, childLeaf.position.y) }.plus(childrenLeaves.flatMap {childLeaf -> childLeaf.getLeafLineList() } ).filterNotNull()

    fun getCurrentHeight() : Int =
        if (childrenEmpty()) 0
        else (1 + childrenLeaves.map { childLeaf -> childLeaf.getCurrentHeight() }.reduce { maxHeight : Int, childLeafHeight -> max(maxHeight, childLeafHeight) })

    fun getChildrenLeavesSize(height: Int) : Int

    companion object {

        val ViewPortXRangePx = 300
        val ViewPortYRangePx = 200
        val LeafDistancePx = (ViewPortXRangePx / 100) * (ViewPortYRangePx / 100) * 10

        fun getLeafDistancePxProb(): Int = ProbabilitySelect.psAccumulating(
            listOf(
                LeafDistancePx,
                LeafDistancePx / 2,
                LeafDistancePx * 3 / 2,
                LeafDistancePx * 3 / 4,
                LeafDistancePx * 5 / 4,
                LeafDistancePx * 1 / 4,
                LeafDistancePx * 7 / 4
            )
        ).getSelectedProbability()!!

        fun getParentPosition(parentLeaf: MutableList<ILeaf>): Point =
            if (!parentLeaf.isNullOrEmpty()) parentLeaf[0].position else Point(256, 256)

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

        fun List<ILeaf>.getLeafList(): List<ILeaf> {
            val returnLeafList = mutableListOf<ILeaf>()

            this.forEach { iLeaf -> iLeaf.getLeafList().forEach { leaf -> returnLeafList.add(leaf) } }

            return returnLeafList
        }

        fun List<ILeaf>.getLeafLineList(): List<Pair<Point, Point>?> {
            val returnLineList = mutableListOf<Pair<Point, Point>?>()

            this.forEach { iLeaf -> iLeaf.getLeafLineList().forEach { line -> returnLineList.add(line) } }

            return returnLineList
        }

        fun ILeaf.addLeaf(childLeaf: ILeaf): ILeaf {

            this.childrenLeaves.add(childLeaf)

            childLeaf.parentLeaf.add(this)

            return this
        }

        fun ILeaf.graftLeaf(childLeaf: ILeaf): ILeaf {

            this.childrenLeaves.add(childLeaf)
            childLeaf.parentLeaf.add(this)

            val xOffset = (this.position.x - childLeaf.position.x).toInt()
            val yOffset = (this.position.y - childLeaf.position.y).toInt()

            childLeaf.position.x += xOffset
            childLeaf.position.y += yOffset

            childLeaf.childrenLeaves.forEach { childSubLeaf -> childSubLeaf.moveLeaf(xOffset, yOffset) }

            return this
        }

        fun ILeaf.moveLeaf(xOffset: Int, yOffset: Int): ILeaf {

            this.position.x += xOffset
            this.position.y += yOffset

            if (!this.childrenEmpty()) this.childrenLeaves.forEach { childSubLeaf ->
                childSubLeaf.moveLeaf(
                    xOffset,
                    yOffset
                )
            }

            return this
        }


        fun ILeaf.nodeLinks(nodes: MutableList<Node>): MutableList<NodeLink> {
            val returnNodeLinks = mutableListOf<NodeLink>()

            if (!parentEmpty()) returnNodeLinks.addNodeLink(nodes, this.uuid, getParentLeaf()!!.uuid)

            if (!childrenEmpty()) this.getChildrenLeavesList()!!
                .forEach { childLeaf -> returnNodeLinks.addNodeLink(nodes, this.uuid, childLeaf.uuid) }

            return returnNodeLinks
        }

        fun ILeaf.nodes(): MutableList<Node> {
            val returnNodes = mutableListOf<Node>()

            returnNodes.addNode(Node(this))

            if (!parentEmpty()) returnNodes.addNode(Node(getParentLeaf()!!))

            if (!childrenEmpty()) this.getChildrenLeavesList()!!
                .forEach { childLeaf -> returnNodes.addNode(Node(childLeaf)) }

            return returnNodes
        }

        fun ILeaf.nodeMesh(): NodeMesh = NodeMesh(nodes = this.nodes(), nodeLinks = this.nodeLinks(this.nodes()))

        fun List<ILeaf>.nodes(): MutableList<Node> {
            val returnNodes = mutableListOf<Node>()

            this.forEach { iLeaf -> returnNodes.addNode(Node(iLeaf)) }

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