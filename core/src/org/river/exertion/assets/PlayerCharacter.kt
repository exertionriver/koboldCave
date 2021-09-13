package org.river.exertion.assets

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import org.river.exertion.*
import org.river.exertion.koboldCave.Line.Companion.getPositionByDistanceAndAngle
import org.river.exertion.koboldCave.node.Node.Companion.angleBetween
import org.river.exertion.screen.RenderPalette
import space.earlygrey.shapedrawer.JoinType

object PlayerCharacter {

    fun render(batch : Batch, currentPos : Point, currentAngle : Angle) {

        val pcSdc = ShapeDrawerConfig(batch, RenderPalette.ForeColors[7])
        val pcDrawer = pcSdc.getDrawer()

        val ego : com.badlogic.gdx.utils.Array<Vector2> = com.badlogic.gdx.utils.Array()

        ego.clear()

        val bottomArrow = currentPos.getPositionByDistanceAndAngle(4f, (currentAngle + 180f).normalizeDeg())
        val topArrow = currentPos.getPositionByDistanceAndAngle(6f, currentAngle)
        val tipArrowLeft = topArrow.getPositionByDistanceAndAngle(3f, (currentAngle + 150f).normalizeDeg())
        val tipArrowRight = topArrow.getPositionByDistanceAndAngle(3f, (currentAngle - 150f).normalizeDeg())

        ego.add(bottomArrow, topArrow)
        ego.add(topArrow, tipArrowLeft)
        ego.add(tipArrowLeft, tipArrowRight)
        ego.add(tipArrowRight, topArrow)

        //may have to use another (colored) texture for ego-path
        pcDrawer.path(ego, 1f, JoinType.SMOOTH, true)
    }
}