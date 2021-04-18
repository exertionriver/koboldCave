package render

import com.soywiz.korim.color.Colors
import com.soywiz.korim.text.TextAlignment

object RenderPalette {

    val ForeColors = listOf(Colors["#42f048"], Colors["#777aff"], Colors["#f4ff0b"], Colors["#ff4494"]
        , Colors["#b685ff"], Colors["#53ffec"], Colors["#f08154"])

    val BackColors = listOf(Colors["#20842b"], Colors["#4646b6"], Colors["#9f9a3f"], Colors["#9f3762"]
        , Colors["#7e519f"], Colors["#49989f"], Colors["#844a32"])

    val TextColor = Colors.AZURE
    val TextSize = 24.0
    val TextAlign = TextAlignment.BASELINE_LEFT
}