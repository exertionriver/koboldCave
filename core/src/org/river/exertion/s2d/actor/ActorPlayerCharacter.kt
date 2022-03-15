package org.river.exertion.s2d.actor

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.ui.Image
import org.river.exertion.*
import org.river.exertion.geom.Line.Companion.getPositionByDistanceAndAngle
import space.earlygrey.shapedrawer.JoinType

class ActorPlayerCharacter(initName : String = "PlayerCharacter", initPosition : Point, initAngle : Angle) : Image(), IBaseActor {

    override var actorName: String = initName
    override var currentPosition = initPosition
    override var currentAngle = initAngle

    init {
        name = initName
        x = initPosition.x
        y = initPosition.y
        rotation = initAngle
        MessageManager.getInstance().addListener(this, MessageIds.ECS_S2D_BRIDGE.id())
    }

    override fun draw(batch : Batch, parentAlpha : Float) {
        x = currentPosition.x
        y = currentPosition.y
        rotation = currentAngle

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

//            Gdx.app.log("render","scaleX: $scaleX")

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