package org.river.exertion.geom.leaf

import org.river.exertion.*
import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.geom.Line.Companion.getPositionByDistanceAndAngle
import org.river.exertion.geom.node.Node
import org.river.exertion.geom.node.Node.Companion.addNode
import org.river.exertion.geom.node.NodeLink
import org.river.exertion.geom.node.NodeLink.Companion.addNodeLink
import org.river.exertion.geom.node.nodeMesh.NodeMesh
import org.river.exertion.koboldQueue.condition.ProbabilitySelect
import java.util.*

interface ILeaf {

    val uuid : UUID

    val description : String

    val topHeight : Int

    val height : Int

    val position : Point

    val distanceFromParent : Float

    val topAngle : Angle

    val angleFromParent : Angle

    val cumlAngleFromTop : Angle

    val parent : ILeaf?

    fun parentEmpty() = (parent == null)

    val children : MutableSet<ILeaf>

    fun childrenEmpty() = children.isEmpty()

    fun getChildrenSet() : Set<ILeaf>? = if ( childrenEmpty() ) null else children.toSet()

    fun getChildrenSize(height: Int, topHeight : Int = height) : Int

    fun getVarianceChildAngle(variance : Angle) : Angle =
        this.angleFromParent + Probability(0, variance).getValue()

    fun getConvergentChildAngle(variance : Angle, convergeToAngle : Angle = this.angleFromParent ) : Angle =
        ( convergeToAngle.times(2) + getVarianceChildAngle(variance).times(2) ) / 4

    fun getSet() : Set<ILeaf> =
        if (childrenEmpty()) setOf(this)
        else setOf(this).plus(children.flatMap { child -> child.getSet() } )

    fun getLineSet() : Set<Pair<Point, Point>?> =
        if (childrenEmpty()) setOf(null)
        else children.map {
            childLeaf -> Point(this.position.x, this.position.y) to Point(childLeaf.position.x, childLeaf.position.y)
        }.plus(children.flatMap {
            childLeaf -> childLeaf.getLineSet()
        } ).filterNotNull().toSet()

    companion object {

        fun getNextDistancePxProb(): Float = ProbabilitySelect.psAccumulating(
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

        fun getParentPosition(parent: ILeaf?): Point = parent?.position ?: Point(-1.0F, -1.0F)

        fun getChildPosition(parentPosition: Point, distanceFromParent: Float, childAngle: Angle): Point {
            return parentPosition.getPositionByDistanceAndAngle(distanceFromParent, childAngle)
        }

        fun ILeaf.node(): Node {
            return Node(this.uuid, this.position.round(), this.description)
        }

        fun ILeaf.nodeLinks(nodes: MutableSet<Node>): MutableSet<NodeLink> {
            val returnNodeLinks = mutableSetOf<NodeLink>()

            this.getSet().forEach {

                if (!it.parentEmpty()) returnNodeLinks.addNodeLink(nodes, it.uuid, it.parent!!.uuid)

                if (!it.childrenEmpty()) it.getChildrenSet()!!
                    .forEach { childLeaf -> returnNodeLinks.addNodeLink(nodes, it.uuid, childLeaf.uuid) }
            }

            return returnNodeLinks
        }

        fun ILeaf.nodes(): MutableSet<Node> {
            val returnNodes = mutableSetOf<Node>()

//            println("list: ${this.getList()}")

            this.getSet().forEach {
                returnNodes.addNode( it.node() )
            }

//            println("leaf nodes: $returnNodes")

            return returnNodes
        }

        fun ILeaf.nodeMesh(): NodeMesh {
            val leafNodes = this.nodes()

            return NodeMesh(nodes = leafNodes, nodeLinks = this.nodeLinks(leafNodes))//.apply { this.processMesh() }
        }

    }
}