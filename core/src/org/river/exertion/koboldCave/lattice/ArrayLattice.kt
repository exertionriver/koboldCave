package org.river.exertion.koboldCave.lattice

import org.river.exertion.koboldCave.lattice.ILattice.Companion.getArrayedChildPosition
import org.river.exertion.koboldCave.lattice.ILattice.Companion.getChildPosition
import org.river.exertion.koboldCave.lattice.ILattice.Companion.getNextDistancePxProb
import org.river.exertion.koboldCave.lattice.ILattice.Companion.getParentPosition
import org.river.exertion.Angle
import org.river.exertion.Point
import java.util.*
import kotlin.random.Random

class ArrayLattice(override val topHeight : Int = 3
                   , override val height : Int = topHeight
                   , override val description: String = "${ArrayLattice::class.simpleName}${Random.nextInt(256)}"
                   , override val parent : ILattice? = null
                   , override val distanceFromParent : Float = 0F
                   , override val topAngle : Angle = 270F // 270 == down
                   , override val angleFromParent : Angle = topAngle
                   , override val cumlAngleFromTop : Angle = topAngle
                   , override val position : Point = if (height == topHeight - 1)
                  getArrayedChildPosition(getParentPosition(parent), topAngle, angleFromParent)
                  else getChildPosition(getParentPosition(parent), distanceFromParent, angleFromParent)
    ) : ILattice {

    override val uuid : UUID = UUID.randomUUID()

    override val children: MutableList<ILattice> = if (height == 0) mutableListOf()
        else MutableList(size = getChildrenSize(height, topHeight)) {
            ArrayLattice(topHeight = topHeight
                , description = description
                , height = height - 1
                , parent = this
                , topAngle = topAngle
                , angleFromParent = if (height == topHeight)
                    this.getVarianceChildAngle(60F) else
                        this.getConvergentChildAngle(4F, topAngle)
                , cumlAngleFromTop = cumlAngleFromTop + (topAngle - angleFromParent)
                , distanceFromParent = getNextDistancePxProb()
            )
    }

    override fun getChildrenSize(height : Int, topHeight : Int): Int {
        return when {
            (topHeight == height) -> Random.nextInt(10) + 5
            else -> 1
        }
    }

    override fun toString() = "${ArrayLattice::class.simpleName}($uuid) : topHeight:${topHeight}, curHeight:${height}, $position, dist:$distanceFromParent, $angleFromParent, ${this.parent}, ${this.children.size}"

}