package render

import com.soywiz.korge.input.onClick
import com.soywiz.korge.ui.uiTextButton
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korma.geom.Point

object RenderPalette {

    val ForeColors = listOf(Colors["#42f048"], Colors["#777aff"], Colors["#f4ff0b"], Colors["#ff4494"]
        , Colors["#b685ff"], Colors["#53ffec"], Colors["#f08154"])

    val BackColors = listOf(Colors["#20842b"], Colors["#4646b6"], Colors["#9f9a3f"], Colors["#9f3762"]
        , Colors["#7e519f"], Colors["#49989f"], Colors["#844a32"])

    val TextColor = Colors.AZURE
    val TextSize = 24.0
    val TextAlignLeft = TextAlignment.BASELINE_LEFT
    val TextAlignRight = TextAlignment.BASELINE_RIGHT

    var returnClick : ButtonCommand? = null

    suspend fun initDemoScreen(renderContainer : Container) : Map<CommandView, View> {

        val labelTextPosition = Point(renderContainer.width.toInt() - 50, 40)
        val descriptionTextPosition = Point(renderContainer.width.toInt() - 50, 65)
        val commentTextPosition = Point(renderContainer.width.toInt() - 50, 90)
        val loadingTextPosition = Point(renderContainer.width.toInt() / 2, renderContainer.height.toInt() / 2)
        val prevClickablePosition = Point(renderContainer.width.toInt() - 50, 0)
        val nextClickablePosition = Point(renderContainer.width.toInt() - 50, 50)

        val returnMap = mapOf(
            CommandView.LABEL_TEXT to renderContainer.text(text = CommandView.LABEL_TEXT.label(), color = TextColor, textSize = TextSize, alignment = TextAlignRight).position(labelTextPosition)
            , CommandView.DESCRIPTION_TEXT to renderContainer.text(text = CommandView.DESCRIPTION_TEXT.label(), color = TextColor, textSize = TextSize, alignment = TextAlignRight).position(descriptionTextPosition)
            , CommandView.COMMENT_TEXT to renderContainer.text(text = CommandView.COMMENT_TEXT.label(), color = TextColor, textSize = TextSize, alignment = TextAlignRight).position(commentTextPosition)
            , CommandView.LOADING_TEXT to renderContainer.text(text = CommandView.LOADING_TEXT.label(), color = TextColor, textSize = TextSize, alignment = TextAlignRight).position(loadingTextPosition)
            , CommandView.PREV_CLICKABLE to renderContainer.uiTextButton(text= CommandView.PREV_CLICKABLE_TEXT.label(), width = 50.0, height = 50.0).position(prevClickablePosition)
            , CommandView.NEXT_CLICKABLE to renderContainer.uiTextButton(text= CommandView.NEXT_CLICKABLE_TEXT.label(), width = 50.0, height = 50.0).position(nextClickablePosition)
        )

        returnMap[CommandView.PREV_CLICKABLE].onClick { println ("prev clicked!") ; returnClick = ButtonCommand.PREV }
        returnMap[CommandView.NEXT_CLICKABLE].onClick { println ("next clicked!") ; returnClick = ButtonCommand.NEXT }

        return returnMap
    }
}