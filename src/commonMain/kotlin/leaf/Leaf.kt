package leaf

import Probability
import ProbabilitySelect
import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.minus
import com.soywiz.korma.geom.plus
import leaf.ILeaf.Companion.getChildPosition
import leaf.ILeaf.Companion.getNextDistancePxProb
import leaf.ILeaf.Companion.getParentPosition
import node.INodeMesh
import node.NodeMesh
import kotlin.random.Random

@ExperimentalUnsignedTypes
class Leaf(override val topHeight : Int = 3
            , override val height : Int = topHeight
            , override val description: String = "${Leaf::class.simpleName}${Random.nextInt(256)}"
            , override val parent : MutableList<ILeaf> = mutableListOf()
            , override val distanceFromParent : Int = 0
            , override val topAngle : Angle = Angle.fromDegrees(270.0) // 270 == down
            , override val angleFromParent : Angle = topAngle
            , override val cumlAngleFromTop : Angle = topAngle
            , override val position : Point = getChildPosition(getParentPosition(parent), distanceFromParent, angleFromParent)
    ) : ILeaf {

    override val uuid : UUID = UUID.randomUUID(Random.Default)

    override val children: MutableList<ILeaf> = if (height == 0) mutableListOf()
        else MutableList(size = getChildrenSize(height)) {
            Leaf(topHeight = topHeight
                , description = description
                , height = height - 1
                , parent = mutableListOf(this)
                , distanceFromParent = getNextDistancePxProb()
                , topAngle = topAngle
                , angleFromParent = this.getVarianceChildAngle(Angle.fromDegrees(30))
                , cumlAngleFromTop = cumlAngleFromTop + (topAngle - angleFromParent)
            )
    }

    override fun getChildrenSize(height : Int, topHeight : Int): Int {
        return when {
            (height > 2) -> ProbabilitySelect(
                mapOf(
                    "1" to Probability(35, 0),
                    "2" to Probability(60, 0),
                    "3" to Probability(5, 0)
                )
            ).getSelectedProbability()!!.toInt()
            (height > 0) -> ProbabilitySelect(
                mapOf(
//                    "0" to Probability(5, 0),
                    "1" to Probability(25, 0),
                    "2" to Probability(50, 0),
                    "3" to Probability(25, 0)
                )
            ).getSelectedProbability()!!.toInt()
            else -> 0
        }
    }

    override fun toString() = "${Leaf::class.simpleName}($uuid) : topHeight:${topHeight}, curHeight:${height}, $position, dist:$distanceFromParent, $angleFromParent, ${getParent()?.uuid}, ${children.size}"

    companion object {

        fun emptyLeaf() = Leaf(topHeight = 0)

    }

}