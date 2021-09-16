package org.river.exertion.koboldCave.lattice

import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldCave.Line.Companion.getArrayedPositionByAngle
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.NextDistancePx
import org.river.exertion.koboldCave.Line.Companion.getPositionByDistanceAndAngle
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.Node.Companion.addNode
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.addNodeLink
import org.river.exertion.koboldCave.node.nodeMesh.NodeMesh
import org.river.exertion.Angle
import org.river.exertion.Point
import org.river.exertion.koboldQueue.condition.ProbabilitySelect
import org.river.exertion.koboldCave.node.NodeLink.Companion.addNodeLinks
import org.river.exertion.round
import java.util.*

@ExperimentalUnsignedTypes
interface ILattice {

    val uuid : UUID

    val description : String

    val topHeight : Int

    val height : Int

    val position : Point

    val distanceFromParent : Float

    val topAngle : Angle

    val angleFromParent : Angle

    val cumlAngleFromTop : Angle

    val parent : ILattice?

    fun parentEmpty() = (parent == null)

    val children : MutableList<ILattice>

    fun childrenEmpty() = children.isEmpty()

    fun getChildrenList() : List<ILattice>? = if ( childrenEmpty() ) null else children.toList()

    fun getChildrenSize(height: Int, topHeight : Int = height) : Int

    fun getVarianceChildAngle(variance : Angle) : Angle =
        this.angleFromParent + Probability(0, variance).getValue()

    fun getConvergentChildAngle(variance : Angle, convergeToAngle : Angle = this.angleFromParent ) : Angle =
        ( convergeToAngle.times(2) + getVarianceChildAngle(variance).times(2) ) / 4

    fun getList() : List<ILattice> =
        if (childrenEmpty()) listOf(this)
        else listOf(this).plus(children.flatMap { child -> child.getList() } ).distinct()

    fun getPrimaryLineList() : List<Pair<Point, Point>?> =
        if (childrenEmpty()) listOf(null)
        else children.map {
            childLattice -> Point(this.position.x, this.position.y) to Point(childLattice.position.x, childLattice.position.y)
        }.plus(children.flatMap {
            childLattice -> childLattice.getPrimaryLineList()
        } ).filterNotNull().distinct()

    companion object {

        fun getNextDistancePxProb(): Float = ProbabilitySelect.psAccumulating(
            listOf(
                NextDistancePx
                , NextDistancePx * 15 / 16
                , NextDistancePx * 17 / 16
            )
        ).getSelectedProbability()!!

        fun getParentPosition(parent: ILattice?): Point = parent?.position ?: Point(-1.0F, -1.0F)

        fun getChildPosition(parentPosition: Point, distanceFromParent: Float, childAngle: Angle): Point {

            return parentPosition.getPositionByDistanceAndAngle(distanceFromParent, childAngle)
        }

        fun getArrayedChildPosition(parentPosition: Point, topAngle: Angle, childAngle: Angle): Point {

            return parentPosition.getArrayedPositionByAngle(topAngle, childAngle)
        }

        fun List<ILattice>.getLateralLineList(): List<Pair<Point, Point>?> {
            val returnLineList = mutableListOf<Pair<Point, Point>?>()

            val topLatticeHeight = this[0].topHeight

            (0..topLatticeHeight).forEach { curHeight ->
                val sortedILattices = this.filter {
                        iLattice -> iLattice.height == curHeight
                }.sortedBy {
                        filteredILattice -> filteredILattice.cumlAngleFromTop
                }

                sortedILattices.forEachIndexed { iLatticeIdx, sortedILattice ->
                    if (iLatticeIdx > 0) returnLineList.add(Pair(sortedILattice.position, sortedILattices[iLatticeIdx - 1].position))
                }
            }

            return returnLineList.filterNotNull()
        }

        fun ILattice.getLineList(): List<Pair<Point, Point>?> {
            val returnLineList = mutableListOf<Pair<Point, Point>?>()

            returnLineList.addAll(this.getPrimaryLineList())

            returnLineList.addAll(this.getList().getLateralLineList())

            return returnLineList
        }

        fun List<ILattice>.getLateralNodeLinkList(): MutableList<NodeLink> {
            val returnLineList = mutableListOf<NodeLink>()

            val topLatticeHeight = this[0].topHeight

            (0..topLatticeHeight).forEach { curHeight ->
                val sortedILattices = this.filter {
                        iLattice -> iLattice.height == curHeight
                }.sortedBy {
                        filteredILattice -> filteredILattice.cumlAngleFromTop
                }

                sortedILattices.forEachIndexed { iLatticeIdx, sortedILattice ->
                    if (iLatticeIdx > 0) returnLineList.add(NodeLink(sortedILattice.uuid, sortedILattices[iLatticeIdx - 1].uuid))
                }
            }

            return returnLineList
        }

        fun ILattice.node(): Node {
            return Node(this.uuid, this.position.round(), this.description)
        }

        fun ILattice.nodeLinks(nodes: MutableList<Node>): MutableList<NodeLink> {
            val returnNodeLinks = mutableListOf<NodeLink>()

            this.getList().forEach {

                if (!it.parentEmpty()) returnNodeLinks.addNodeLink(nodes, it.uuid, it.parent!!.uuid)

                if (!it.childrenEmpty()) it.getChildrenList()!!
                    .forEach { childLeaf -> returnNodeLinks.addNodeLink(nodes, it.uuid, childLeaf.uuid) }
            }

            val lateralNodeLinks = this.getList().getLateralNodeLinkList()

            if (lateralNodeLinks.isNotEmpty()) returnNodeLinks.addNodeLinks(nodes, lateralNodeLinks)

            return returnNodeLinks
        }

        fun ILattice.nodes(): MutableList<Node> {
            val returnNodes = mutableListOf<Node>()

//            println("list: ${this.getList()}")

            this.getList().forEach {
                returnNodes.addNode( it.node() )
            }

//            println("leaf nodes: $returnNodes")

            return returnNodes
        }

        fun ILattice.nodeMesh(): NodeMesh {
            val latticeNodes = this.nodes()

                return NodeMesh(nodes = latticeNodes, nodeLinks = this.nodeLinks(latticeNodes))//.apply { this.processMesh() }

            }
    }
}