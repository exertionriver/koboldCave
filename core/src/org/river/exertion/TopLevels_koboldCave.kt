package org.river.exertion

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Align
import org.river.exertion.geom.node.Node
import space.earlygrey.shapedrawer.ShapeDrawer
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt

typealias Point = Vector2

operator fun Point.plus(secondPoint : Point) : Point = Point(this.x + secondPoint.x, this.y + secondPoint.y)
operator fun Point.minus(secondPoint : Point) : Point = Point(this.x - secondPoint.x, this.y - secondPoint.y)
operator fun Point.times(multiplier : Int) : Point = Point(this.x * multiplier, this.y * multiplier)
operator fun Point.div(divisor : Int) : Point = Point(this.x / divisor, this.y / divisor)

fun Point.trunc() : Point = Point(this.x.toInt().toFloat(), this.y.toInt().toFloat())
fun Point.round() : Point = Point(this.x.roundToInt().toFloat(), this.y.roundToInt().toFloat())

fun middle(firstPoint : Point, secondPoint : Point) : Point = Point((firstPoint.x + secondPoint.x) / 2, (firstPoint.y + secondPoint.y) / 2)

typealias Angle = Float
fun Angle.normalizeDeg() : Angle {
    var returnAngle = this

    while ( returnAngle >= 360 ) returnAngle -= 360
    while ( returnAngle < 0 ) returnAngle += 360

    return returnAngle
}

fun Angle.normalizeRad() : Angle {
    var returnAngle = this

    while ( returnAngle >= 2 * PI.toFloat() ) returnAngle -= 2 * PI.toFloat()
    while ( returnAngle < 0 ) returnAngle += 2 * PI.toFloat()

    return returnAngle
}

operator fun Angle.plus(secondAngle : Angle) = (this + secondAngle).normalizeDeg()
operator fun Angle.minus(secondAngle : Angle) = (this - secondAngle).normalizeDeg()

fun Angle.radians(): Float = (this * PI.toFloat() / 180F).normalizeRad()
fun Angle.degrees(): Float = (this * 180F / PI.toFloat()).normalizeDeg()

fun Angle.leftAngleBetween(angleToTheLeft: Angle) = when {
    (angleToTheLeft < this) -> (360f - (this - angleToTheLeft)).normalizeDeg()
    else -> (angleToTheLeft - this)
}

fun Angle.rightAngleBetween(angleToTheRight: Angle) = when {
    (angleToTheRight > this) -> (360f - (angleToTheRight - this)).normalizeDeg()
    else -> (this - angleToTheRight)
}

fun BitmapFont.drawLabel(batch : Batch, location : Point, labelText : String, color : Color, fontSize : Int = 32) {

    val labelLayout = GlyphLayout(this, labelText, color, (fontSize * labelText.length).toFloat(), Align.left,true )
    this.draw(batch, labelLayout, location.x - labelLayout.width / 2 , location.y - labelLayout.height / 2)
}

//https://github.com/earlygrey/shapedrawer/wiki/Using-Shape-Drawer
class ShapeDrawerConfig(val batch: Batch, val baseColor : Color = Color.WHITE) {

    lateinit var pixmap : Pixmap
    lateinit var texture : Texture
    lateinit var textureRegion: TextureRegion
    lateinit var shapeDrawer: ShapeDrawer

    init {
        pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(baseColor)
        pixmap.drawPixel(0, 0)

        texture = Texture(pixmap, true)
        textureRegion = TextureRegion(texture, 0, 0, 1, 1)
        shapeDrawer = ShapeDrawer(this.batch, textureRegion)
    }

    fun getDrawer() = shapeDrawer

    fun disposeShapeDrawerConfig() {
        this.texture.dispose()
        this.pixmap.dispose()
    }
}

val NextDistancePx = Game.initViewportWidth / Game.initViewportHeight * 16F

class InputProcessorHandler(val renderCamera: OrthographicCamera, val nodes: MutableSet<Node>) : InputProcessor {
    override fun keyDown(keycode: Int): Boolean {
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        return true
    }

    override fun keyTyped(character: Char): Boolean {
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Input.Buttons.LEFT) {
            val point = Vector3()
            renderCamera.unproject(point.set(screenX.toFloat(), screenY.toFloat(), 0f))
            val clickedNode = nodes.filter { abs(it.position.x - point.x) < 5 && abs(it.position.y - point.y) < 5 }.firstOrNull()
            println("$clickedNode: ${point.x}, ${point.y}")
        }
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return true
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return true
    }

}

object InputHandler {

    fun handleInput(camera : OrthographicCamera) {
        when {
            Gdx.input.isKeyJustPressed(Input.Keys.W) -> { camera.position.y += 50f }
            Gdx.input.isKeyJustPressed(Input.Keys.S) -> { camera.position.y -= 50f }
            Gdx.input.isKeyJustPressed(Input.Keys.A) -> { camera.position.x -= 50f }
            Gdx.input.isKeyJustPressed(Input.Keys.D) -> { camera.position.x += 50f }
            Gdx.input.isKeyJustPressed(Input.Keys.Q) -> { camera.zoom -= 0.1f }
            Gdx.input.isKeyJustPressed(Input.Keys.E) -> { camera.zoom += 0.1f }
//                Gdx.input.isKeyJustPressed(Input.Keys.Q) -> {println("leftRot!"); camera.rotate(-30F) }
//                Gdx.input.isKeyJustPressed(Input.Keys.E) -> {println("rightRot!"); camera.rotate(30F) }
//                else -> //do nothing
        }
    }


    fun handleInput(camera : PerspectiveCamera, refPoint : Point) {
        when {
            Gdx.input.isKeyJustPressed(Input.Keys.W) -> { camera.position.y += 10f }
            Gdx.input.isKeyJustPressed(Input.Keys.S) -> { camera.position.y -= 10f }
            Gdx.input.isKeyJustPressed(Input.Keys.A) -> { camera.position.x -= 10f }
            Gdx.input.isKeyJustPressed(Input.Keys.D) -> { camera.position.x += 10f }
            Gdx.input.isKeyJustPressed(Input.Keys.Q) -> { camera.position.z -= 10f }
            Gdx.input.isKeyJustPressed(Input.Keys.E) -> { camera.position.z += 10f }
//usually, it is better to use Quaternion

                Gdx.input.isKeyJustPressed(Input.Keys.R) -> { camera.rotateAround(Vector3(refPoint.x, refPoint.y, 0f), Vector3(1f, 0f, 0f), 30f) }
                Gdx.input.isKeyJustPressed(Input.Keys.F) -> { camera.rotateAround(Vector3(refPoint.x, refPoint.y, 0f), Vector3(0f, 1f, 0f), 30f) }
                Gdx.input.isKeyJustPressed(Input.Keys.V) -> { camera.rotateAround(Vector3(refPoint.x, refPoint.y, 0f), Vector3(0f, 0f, 1f), 30f) }
//                else -> //do nothing
        }
    }
}

enum class MessageIds {
    ECS_S2D_BRIDGE,
    S2D_ECS_BRIDGE,
    PLAN_BRIDGE,
    PERCEPTION_BRIDGE,
    FEELING_BRIDGE;

    fun id() = this.ordinal
}
