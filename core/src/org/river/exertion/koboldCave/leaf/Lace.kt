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

@ExperimentalUnsignedTypes
class Lace(override val topHeight : Int = 3
           , override val height : Int = topHeight
           , override val description: String = "${Lace::class.simpleName}${Random.nextInt(256)}"
           , override val parent : ILeaf? = null
           , override val distanceFromParent : Float = 0F
           , override val topAngle : Angle = 270F // 270 == down
           , override val angleFromParent : Angle = topAngle
           , override val cumlAngleFromTop : Angle = topAngle
           , override val position : Point = getChildPosition(getParentPosition(parent), distanceFromParent, angleFromParent)
    ) : ILeaf {

    override val uuid : UUID = UUID.randomUUID()

    override val children: MutableList<ILeaf> = if (height == 0) mutableListOf()
        else MutableList(size = getChildrenSize(height)) {
            Lace(topHeight = topHeight
                , description = description
                , height = height - 1
                , parent = this
                , distanceFromParent = getNextDistancePxProb()
                , topAngle = topAngle
                , angleFromParent = this.getConvergentChildAngle(60F)
                , cumlAngleFromTop = cumlAngleFromTop + (topAngle - angleFromParent)
            )
    }

    override fun getChildrenSize(height : Int, topHeight : Int): Int {
        return ProbabilitySelect(
                mapOf(
                    "1" to Probability(90, 0)
                    , "2" to Probability(10, 0)
                )
            ).getSelectedProbability()!!.toInt()
    }

    @ExperimentalUnsignedTypes
    override fun toString() = "${Lace::class.simpleName}($uuid) : topHeight:${topHeight}, curHeight:${height}, $position, dist:$distanceFromParent, $angleFromParent, parent:${this.parent}, children:${this.children.size}"

}