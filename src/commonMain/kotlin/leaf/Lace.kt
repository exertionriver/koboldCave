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
import kotlin.random.Random

@ExperimentalUnsignedTypes
class Lace(override val topHeight : Int = 3
           , override val height : Int = topHeight
           , override val description: String = "${Lace::class.simpleName}${Random.nextInt(256)}"
           , override val parent : MutableList<ILeaf> = mutableListOf()
           , override val distanceFromParent : Int = 0
           , override val topAngle : Angle = Angle.fromDegrees(270.0) // 270 == down
           , override val angleFromParent : Angle = topAngle
           , override val cumlAngleFromTop : Angle = topAngle
           , override val refILeaf : ILeaf? = null
           , override val position : Point = getChildPosition(getParentPosition(parent), distanceFromParent, angleFromParent)
    ) : ILeaf {

    override val uuid : UUID = UUID.randomUUID(Random.Default)

    override val children: MutableList<ILeaf> = if (height == 0) mutableListOf()
        else MutableList(size = getChildrenSize(height)) {
            Lace(topHeight = topHeight
                , description = description
                , height = height - 1
                , parent = mutableListOf(this)
                , distanceFromParent = getNextDistancePxProb()
                , topAngle = topAngle
                , angleFromParent = if (refILeaf != null) this.getBorderingChildAngle(Angle.fromDegrees(30), refILeaf = refILeaf) //experimental at v0.3
                    else this.getConvergentChildAngle(Angle.fromDegrees(60), topAngle)
                , cumlAngleFromTop = cumlAngleFromTop + (topAngle - angleFromParent)
                , refILeaf = refILeaf
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
    override fun toString() = "${Lace::class.simpleName}($uuid) : topHeight:${topHeight}, curHeight:${height}, $position, dist:$distanceFromParent, $angleFromParent, ${getParent()?.uuid}, ${children.size}"

    companion object {

        fun emptyLeaf() = Lace(topHeight = 0)

    }

}