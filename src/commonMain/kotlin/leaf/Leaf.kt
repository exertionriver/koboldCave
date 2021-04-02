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
class Leaf(override val initHeight : Int = 3
        , override val description: String = "leaf${Random.nextInt(256)}"
        , override val parentLeaf : MutableList<ILeaf> = mutableListOf()
        , override val distanceFromParent : Int = 0
        , override val angleFromParent : Angle = Angle.fromDegrees(270.0) //270 == down
        , override val position : Point = getChildPosition(getParentPosition(parentLeaf), distanceFromParent, angleFromParent)
    ) : ILeaf {

    override val uuid : UUID = UUID.randomUUID(Random.Default)

    override val childrenLeaves: MutableList<ILeaf> = if (initHeight == 0) mutableListOf()
        else MutableList(size = getChildrenLeavesSize(initHeight)) {
            Leaf(initHeight = initHeight - 1
                , parentLeaf = mutableListOf(this)
                , distanceFromParent = getLeafDistancePxProb()
                , angleFromParent = this.getChildAngle(Angle.fromDegrees(30))
            )
    }

    override fun getChildrenLeavesSize(height : Int): Int {
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
                    "0" to Probability(5, 0),
                    "1" to Probability(40, 0),
                    "2" to Probability(50, 0),
                    "3" to Probability(5, 0)
                )
            ).getSelectedProbability()!!.toInt()
            else -> 0
        }
    }

    override fun toString() = "${Leaf::class.simpleName}($uuid) : height:${initHeight}, curHeight:${getCurrentHeight()}, $position, dist:$distanceFromParent, $angleFromParent, ${getParentLeaf()?.uuid}, ${childrenLeaves.size}"

    companion object {

        fun emptyLeaf() = Leaf(initHeight = 0)

    }

}