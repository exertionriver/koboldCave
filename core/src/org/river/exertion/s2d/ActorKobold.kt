package org.river.exertion.s2d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import org.river.exertion.*
import org.river.exertion.geom.Line.Companion.getPositionByDistanceAndAngle
import org.river.exertion.geom.Line.Companion.onSegment
import org.river.exertion.RenderPalette
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.component.entity.IEntity
import space.earlygrey.shapedrawer.JoinType

class ActorKobold(initName : String, initPosition : Point, initAngle : Angle) : Actor(), IBaseActor {

    override var actorName: String = initName
    override var currentPosition = initPosition
    override var currentAngle = initAngle
    var losMap : MutableMap<Int, Point>? = null

    init {
        name = initName
        x = initPosition.x
        y = initPosition.y
        rotation = initAngle
        MessageManager.getInstance().addListener(this, MessageIds.ECS_S2D_BRIDGE.id())
        MessageManager.getInstance().addListener(this, MessageIds.LOSMAP_BRIDGE.id())
    }

    override fun handleMessage(msg: Telegram?): Boolean {

        if (msg != null && msg.message == MessageIds.LOSMAP_BRIDGE.id() ) {
            losMap = (msg.extraInfo as MutableMap<Int, Point>)
        }

        return super.handleMessage(msg)
    }

    override fun draw(batch : Batch, parentAlpha : Float) {
        x = currentPosition.x
        y = currentPosition.y
        rotation = currentAngle

        if (losMap == null) {
            render(batch, Point(x, y), rotation, scaleX)
        } else {
            renderLos(batch, losMap!!, Point(x, y), rotation)
        }
    }

    companion object {
        fun render(batch : Batch, currentPos : Point, currentAngle : Angle, scaleX : Float = 1f)  {

            val pcSdc = ShapeDrawerConfig(batch, RenderPalette.ForeColors[7])
            val pcDrawer = pcSdc.getDrawer()

            val ego : com.badlogic.gdx.utils.Array<Vector2> = com.badlogic.gdx.utils.Array()

            ego.clear()

//            Gdx.app.log("render","scaleX: $scaleX")

            val bottomFork = currentPos.getPositionByDistanceAndAngle(4f * scaleX, (currentAngle + 180f).normalizeDeg())
            val topFork = currentPos.getPositionByDistanceAndAngle(2f * scaleX, currentAngle.normalizeDeg())
            val tipForkLeft = topFork.getPositionByDistanceAndAngle(3f * scaleX, (currentAngle + 30f).normalizeDeg())
            val tipForkRight = topFork.getPositionByDistanceAndAngle(3f * scaleX, (currentAngle - 30f).normalizeDeg())
            val tipLeft = topFork.getPositionByDistanceAndAngle(3f * scaleX, (currentAngle + 90f).normalizeDeg())
            val tipRight = topFork.getPositionByDistanceAndAngle(3f * scaleX, (currentAngle - 90f).normalizeDeg())

            ego.add(currentPos, bottomFork)
            ego.add(bottomFork, tipForkLeft)
            ego.add(tipForkLeft, tipRight)
            ego.add(tipRight, tipLeft)
            ego.add(tipLeft, tipForkRight)

            //may have to use another (colored) texture for ego-path
            pcDrawer.path(ego, 1f, JoinType.SMOOTH, true)
        }

        fun renderLos(batch : Batch, losMap : MutableMap<Int, Point>, currentPos : Point, currentAngle : Angle) {

            val pcSdc = ShapeDrawerConfig(batch, RenderPalette.ForeColors[7])
            val pcDrawer = pcSdc.getDrawer()

            val losPos = losMap[360]!!

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
}