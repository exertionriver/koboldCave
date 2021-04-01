package render

import com.soywiz.klock.TimeSpan
import com.soywiz.klogger.AnsiEscape
import com.soywiz.korge.Korge
import com.soywiz.korge.resources.resourceBitmap
import com.soywiz.korge.view.*
import com.soywiz.korge.view.tween.rotateTo
import com.soywiz.korim.color.Colors
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.async.delay
import com.soywiz.korio.resources.ResourcesContainer
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.circle
import com.soywiz.korma.geom.vector.line
import leaf.ILeaf
import leaf.ILeaf.Companion.LeafDistancePx
import leaf.ILeaf.Companion.addLeaf
import leaf.ILeaf.Companion.graftLeaf
import leaf.Leaf
import leaf.Stream
import kotlin.random.Random

object RenderArrow {

    val ResourcesContainer.arrow_png by resourceBitmap("brightarrow.png")

    @ExperimentalUnsignedTypes
    suspend fun renderArrow() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val image = image(arrow_png) {
            anchor(1, 1)
            scale(.1)
            position(512, 512)
        }.rotation(Angle.fromDegrees(180))

    }
}