package org.river.exertion.koboldCave.leaf

import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.condition.ProbabilitySelect
import org.river.exertion.Angle
import org.river.exertion.Point
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.getChildPosition
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.getNextDistancePxProb
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.getParentPosition
import java.util.*
import kotlin.random.Random

class Leaf(override val topHeight : Int = 3
            , override val height : Int = topHeight
            , override val description: String = "${Leaf::class.simpleName}${Random.nextInt(256)}"
            , override val parent : ILeaf? = null
            , override val distanceFromParent : Float = 0F
            , override val topAngle : Angle = 270F // 270 == down
            , override val angleFromParent : Angle = topAngle
            , override val cumlAngleFromTop : Angle = topAngle
            , override val position : Point = getChildPosition(getParentPosition(parent), distanceFromParent, angleFromParent)
    ) : ILeaf {

    override val uuid : UUID = UUID.randomUUID()

    override val children: MutableSet<ILeaf> = if (height == 0) mutableSetOf()
        else MutableList(size = getChildrenSize(height, topHeight)) {
            Leaf(topHeight = topHeight
                , description = description
                , height = height - 1
                , parent = this
                , distanceFromParent = getNextDistancePxProb()
                , topAngle = topAngle
                , angleFromParent = this.getVarianceChildAngle(60F)
                , cumlAngleFromTop = cumlAngleFromTop + (topAngle - angleFromParent)
            )
        }.toMutableSet()

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
//                    "0" to org.river.exertion.koboldQueue.condition.Probability(5, 0),
                    "1" to Probability(25, 0),
                    "2" to Probability(50, 0),
                    "3" to Probability(25, 0)
                )
            ).getSelectedProbability()!!.toInt()
            else -> 0
        }
    }

    override fun toString() = "${Leaf::class.simpleName}($uuid) : topHeight:${topHeight}, curHeight:${height}, $position, dist:$distanceFromParent, $angleFromParent, parent:${this.parent}, children:${this.children.size}"

}