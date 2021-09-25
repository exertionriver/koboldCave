package org.river.exertion.koboldCave.leaf

import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldCave.Line.Companion.getPositionByDistanceAndAngle
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.Node.Companion.addNode
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.addNodeLink
import org.river.exertion.koboldCave.node.nodeMesh.NodeMesh
import org.river.exertion.Angle
import org.river.exertion.Game
import org.river.exertion.Point
import org.river.exertion.koboldQueue.condition.ProbabilitySelect
import org.river.exertion.round
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

    val children : MutableList<ILeaf>

    fun childrenEmpty() = children.isEmpty()

    fun getChildrenList() : List<ILeaf>? = if ( childrenEmpty() ) null else children.toList()

    fun getChildrenSize(height: Int, topHeight : Int = height) : Int

    fun getVarianceChildAngle(variance : Angle) : Angle =
        this.angleFromParent + Probability(0, variance).getValue()

    fun getConvergentChildAngle(variance : Angle, convergeToAngle : Angle = this.angleFromParent ) : Angle =
        ( convergeToAngle.times(2) + getVarianceChildAngle(variance).times(2) ) / 4

    fun getList() : List<ILeaf> =
        if (childrenEmpty()) listOf(this)
        else listOf(this).plus(children.flatMap { child -> child.getList() } ).distinct()

    fun getLineList() : List<Pair<Point, Point>?> =
        if (childrenEmpty()) listOf(null)
        else children.map {
            childLeaf -> Point(this.position.x, this.position.y) to Point(childLeaf.position.x, childLeaf.position.y)
        }.plus(children.flatMap {
            childLeaf -> childLeaf.getLineList()
        } ).filterNotNull().distinct()

    companion object {

        val NextDistancePx = Game.initViewportWidth / Game.initViewportHeight * 16F

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

        fun ILeaf.nodeLinks(nodes: MutableList<Node>): MutableList<NodeLink> {
            val returnNodeLinks = mutableListOf<NodeLink>()

            this.getList().forEach {

                if (!it.parentEmpty()) returnNodeLinks.addNodeLink(nodes, it.uuid, it.parent!!.uuid)

                if (!it.childrenEmpty()) it.getChildrenList()!!
                    .forEach { childLeaf -> returnNodeLinks.addNodeLink(nodes, it.uuid, childLeaf.uuid) }
            }

            return returnNodeLinks
        }

        fun ILeaf.nodes(): MutableList<Node> {
            val returnNodes = mutableListOf<Node>()

//            println("list: ${this.getList()}")

            this.getList().forEach {
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