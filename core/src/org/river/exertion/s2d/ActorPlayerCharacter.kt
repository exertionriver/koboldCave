package org.river.exertion.s2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.ui.Image
import org.river.exertion.*
import org.river.exertion.geom.Line.Companion.getPositionByDistanceAndAngle
import space.earlygrey.shapedrawer.JoinType

class ActorPlayerCharacter(initName : String, initPosition : Point, initAngle : Angle) : Image() {

    init {
        name = initName
        x = initPosition.x
        y = initPosition.y
        rotation = initAngle
    }

    override fun draw(batch : Batch, parentAlpha : Float) {
        render(batch, Point(x, y), rotation, scaleX)
    }

    override fun act(delta: Float) {
        val iter: Iterator<Action> = actions.iterator()
        while (iter.hasNext()) {
            iter.next().act(delta)
        }
    }

    companion object {
        fun render(batch : Batch, currentPos : Point, currentAngle : Angle, scaleX : Float = 1f) {

            val pcSdc = ShapeDrawerConfig(batch, RenderPalette.ForeColors[7])
            val pcDrawer = pcSdc.getDrawer()

            val ego : com.badlogic.gdx.utils.Array<Vector2> = com.badlogic.gdx.utils.Array()

            ego.clear()

            Gdx.app.log("render","scaleX: $scaleX")

            val bottomArrow = currentPos.getPositionByDistanceAndAngle(4f * scaleX, (currentAngle + 180f).normalizeDeg())
            val topArrow = currentPos.getPositionByDistanceAndAngle(6f * scaleX, currentAngle)
            val tipArrowLeft = topArrow.getPositionByDistanceAndAngle(3f * scaleX, (currentAngle + 150f).normalizeDeg())
            val tipArrowRight = topArrow.getPositionByDistanceAndAngle(3f * scaleX, (currentAngle - 150f).normalizeDeg())

            ego.add(bottomArrow, topArrow)
            ego.add(topArrow, tipArrowLeft)
            ego.add(tipArrowLeft, tipArrowRight)
            ego.add(tipArrowRight, topArrow)

            //may have to use another (colored) texture for ego-path
            pcDrawer.path(ego, 1f, JoinType.SMOOTH, true)
        }
    }

}