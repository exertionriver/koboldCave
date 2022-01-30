package org.river.exertion.assets

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import org.river.exertion.*
import org.river.exertion.koboldCave.Line.Companion.getPositionByDistanceAndAngle
import org.river.exertion.koboldCave.Line.Companion.onSegment
import org.river.exertion.koboldCave.screen.RenderPalette
import space.earlygrey.shapedrawer.JoinType

object Kobold {

    fun render(batch : Batch, currentPos : Point, currentAngle : Angle) {

        val pcSdc = ShapeDrawerConfig(batch, RenderPalette.ForeColors[7])
        val pcDrawer = pcSdc.getDrawer()

        val ego : com.badlogic.gdx.utils.Array<Vector2> = com.badlogic.gdx.utils.Array()

        ego.clear()

        val bottomFork = currentPos.getPositionByDistanceAndAngle(4f, (currentAngle + 180f).normalizeDeg())
        val topFork = currentPos.getPositionByDistanceAndAngle(2f, currentAngle.normalizeDeg())
        val tipForkLeft = topFork.getPositionByDistanceAndAngle(3f, (currentAngle + 30f).normalizeDeg())
        val tipForkRight = topFork.getPositionByDistanceAndAngle(3f, (currentAngle - 30f).normalizeDeg())
        val tipLeft = topFork.getPositionByDistanceAndAngle(3f, (currentAngle + 90f).normalizeDeg())
        val tipRight = topFork.getPositionByDistanceAndAngle(3f, (currentAngle - 90f).normalizeDeg())

        ego.add(currentPos, bottomFork)
        ego.add(bottomFork, tipForkLeft)
        ego.add(tipForkLeft, tipRight)
        ego.add(tipRight, tipLeft)
        ego.add(tipLeft, tipForkRight)

        //may have to use another (colored) texture for ego-path
        pcDrawer.path(ego, 1f, JoinType.SMOOTH, true)
    }

    fun renderLos(batch : Batch, losMap : MutableMap<Int, Point>, losPos: Point, currentPos : Point, currentAngle : Angle) {

        val pcSdc = ShapeDrawerConfig(batch, RenderPalette.ForeColors[7])
        val pcDrawer = pcSdc.getDrawer()

        val ego : com.badlogic.gdx.utils.Array<Vector2> = com.badlogic.gdx.utils.Array()

        ego.clear()

        val bottomFork = currentPos.getPositionByDistanceAndAngle(4f, (currentAngle + 180f).normalizeDeg())
        val topFork = currentPos.getPositionByDistanceAndAngle(2f, currentAngle.normalizeDeg())
        val tipForkLeft = topFork.getPositionByDistanceAndAngle(3f, (currentAngle + 30f).normalizeDeg())
        val tipForkRight = topFork.getPositionByDistanceAndAngle(3f, (currentAngle - 30f).normalizeDeg())
        val tipLeft = topFork.getPositionByDistanceAndAngle(3f, (currentAngle + 90f).normalizeDeg())
        val tipRight = topFork.getPositionByDistanceAndAngle(3f, (currentAngle - 90f).normalizeDeg())

        ego.add(currentPos, bottomFork)
        ego.add(bottomFork, tipForkLeft)
        ego.add(tipForkLeft, tipRight)
        ego.add(tipRight, tipLeft)
        ego.add(tipLeft, tipForkRight)

        //may have to use another (colored) texture for ego-path
        var aIter = 0
        var koboldSeen = false

        while (aIter <= 359 && !koboldSeen) {

            if (onSegment(losPos, currentPos, losMap[aIter]!!)) {
                pcDrawer.path(ego, 1f, JoinType.SMOOTH, true)
                koboldSeen = true
            }
            aIter++
        }
    }
}