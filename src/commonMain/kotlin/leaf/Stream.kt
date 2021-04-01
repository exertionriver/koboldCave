package leaf

import Probability
import ProbabilitySelect
import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import leaf.ILeaf.Companion.getChildPosition
import leaf.ILeaf.Companion.getLeafDistancePxProb
import leaf.ILeaf.Companion.getParentPosition
import kotlin.random.Random

@ExperimentalUnsignedTypes
class Stream(override val initHeight : Int = 3
             , override val parentLeaf : MutableList<ILeaf> = mutableListOf()
             , override val distanceFromParent : Int = 0
             , val topAngle : Angle = Angle.fromDegrees(270.0) // 270 == down
             , override val angleFromParent : Angle = Angle.fromDegrees(270.0) //270 == down
             , override val position : Point = getChildPosition(getParentPosition(parentLeaf), distanceFromParent, angleFromParent)
    ) : ILeaf {

    override val uuid : UUID = UUID.randomUUID(Random.Default)

    override val childrenLeaves: MutableList<ILeaf> = if (initHeight == 0) mutableListOf()
        else MutableList(size = getChildrenLeavesSize(initHeight)) {
            Stream(initHeight = initHeight - 1
                , parentLeaf = mutableListOf(this)
                , distanceFromParent = getLeafDistancePxProb()
                , topAngle = topAngle
                , angleFromParent = this.getChildAngle(Angle.fromDegrees(60), topAngle)
            )
    }

    override fun getChildrenLeavesSize(height : Int): Int {
        return ProbabilitySelect(
                mapOf(
                    "1" to Probability(90, 0)
                    , "2" to Probability(10, 0)
                )
            ).getSelectedProbability()!!.toInt()
    }

    @ExperimentalUnsignedTypes
    override fun toString() = "${Stream::class.simpleName}($uuid) : height:${initHeight}, curHeight:${getCurrentHeight()}, $position, dist:$distanceFromParent, $angleFromParent, ${getParentLeaf()?.uuid}, ${childrenLeaves.size}"

    companion object {

        fun emptyLeaf() = Stream(initHeight = 0)

    }

}