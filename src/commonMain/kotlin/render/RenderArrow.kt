package render

import com.soywiz.korge.Korge
import com.soywiz.korge.resources.resourceBitmap
import com.soywiz.korge.view.*
import com.soywiz.korge.view.tween.rotateTo
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.resources.ResourcesContainer
import com.soywiz.korma.geom.*

object RenderArrow {

    @ExperimentalUnsignedTypes
    suspend fun renderArrow() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

    val arrowPng = resourcesVfs["brightarrow.png"].readBitmap()

       val image = image(arrowPng) {
            anchor(.5, .5)
            scale(.1)
            position(512, 512)
        }.rotation(Angle.fromDegrees(180)).rotateTo(Angle.Companion.fromDegrees(0))

    }
}