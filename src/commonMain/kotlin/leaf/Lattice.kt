package leaf

import Probability
import ProbabilitySelect
import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import leaf.ILattice.Companion.getChildPosition
import leaf.ILattice.Companion.getNextDistancePxProb
import leaf.ILattice.Companion.getParentPosition
import kotlin.random.Random

@ExperimentalUnsignedTypes
class Lattice(override val topHeight : Int = 3
              , override val height : Int = topHeight
              , override val description: String = "${Lattice::class.simpleName}${Random.nextInt(256)}"
              , override val parent : MutableList<ILattice> = mutableListOf()
              , override val distanceFromParent : Int = 0
              , override val topAngle : Angle = Angle.fromDegrees(270.0) // 270 == down
              , override val angleFromParent : Angle = topAngle //270 == down
              , override val position : Point = getChildPosition(getParentPosition(parent), distanceFromParent, angleFromParent)
    ) : ILattice {

    override val uuid : UUID = UUID.randomUUID(Random.Default)

    override val children: MutableList<ILattice> = if (height == 0) mutableListOf()
        else MutableList(size = getChildrenSize(height, topHeight)) {
            Lattice(topHeight = topHeight
                , height = height - 1
                , parent = mutableListOf(this)
                , topAngle = topAngle
                , angleFromParent = if (height == topHeight)
                    this.getVarianceChildAngle(Angle.fromDegrees(90)) else
                        this.getConvergentChildAngle(Angle.fromDegrees(5), topAngle)
                , distanceFromParent = getNextDistancePxProb()
            )
    }

    override fun getChildrenSize(height : Int, topHeight : Int): Int {
        return when {
            (topHeight == height) -> 8
            else -> 1
        }
    }

    @ExperimentalUnsignedTypes
    override fun toString() = "${Lattice::class.simpleName}($uuid) : topHeight:${topHeight}, curHeight:${height}, $position, dist:$distanceFromParent, $angleFromParent, ${getParent()?.uuid}, ${children.size}"

    companion object {

        fun emptyLeaf() = Lattice(topHeight = 0)

    }

}