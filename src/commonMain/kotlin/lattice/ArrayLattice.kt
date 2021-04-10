package lattice

import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.minus
import com.soywiz.korma.geom.plus
import lattice.ILattice.Companion.getArrayedChildPosition
import lattice.ILattice.Companion.getChildPosition
import lattice.ILattice.Companion.getNextDistancePxProb
import lattice.ILattice.Companion.getParentPosition
import kotlin.random.Random

@ExperimentalUnsignedTypes
class ArrayLattice(override val topHeight : Int = 3
                   , override val height : Int = topHeight
                   , override val description: String = "${ArrayLattice::class.simpleName}${Random.nextInt(256)}"
                   , override val parent : MutableList<ILattice> = mutableListOf()
                   , override val distanceFromParent : Int = 0
                   , override val topAngle : Angle = Angle.fromDegrees(270.0) // 270 == down
                   , override val angleFromParent : Angle = topAngle
                   , override val cumlAngleFromTop : Angle = topAngle
                   , override val refILattice : ILattice? = null
                   , override val position : Point = if (height == topHeight - 1)
                  getArrayedChildPosition(getParentPosition(parent), topAngle, angleFromParent)
                  else getChildPosition(getParentPosition(parent), distanceFromParent, angleFromParent)
    ) : ILattice {

    override val uuid : UUID = UUID.randomUUID(Random.Default)

    override val children: MutableList<ILattice> = if (height == 0) mutableListOf()
        else MutableList(size = getChildrenSize(height, topHeight)) {
            ArrayLattice(topHeight = topHeight
                , height = height - 1
                , parent = mutableListOf(this)
                , topAngle = topAngle
                , angleFromParent = if (height == topHeight)
                    this.getVarianceChildAngle(Angle.fromDegrees(80)) else
                        this.getConvergentChildAngle(Angle.fromDegrees(4), topAngle)
                , cumlAngleFromTop = cumlAngleFromTop + (topAngle - angleFromParent)
                , distanceFromParent = getNextDistancePxProb()
                , refILattice = refILattice
            )
    }

    override fun getChildrenSize(height : Int, topHeight : Int): Int {
        return when {
            (topHeight == height) -> Random.nextInt(10) + 5
            else -> 1
        }
    }

    @ExperimentalUnsignedTypes
    override fun toString() = "${ArrayLattice::class.simpleName}($uuid) : topHeight:${topHeight}, curHeight:${height}, $position, dist:$distanceFromParent, $angleFromParent, ${getParent()?.uuid}, ${children.size}"

    companion object {

        fun emptyLeaf() = ArrayLattice(topHeight = 0)

    }

}