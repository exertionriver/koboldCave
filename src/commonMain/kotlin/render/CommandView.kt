package render

enum class CommandView {
    LABEL_TEXT { override fun label() = "LABEL_TEXT" }
    , DESCRIPTION_TEXT { override fun label() = "DESCRIPTION_TEXT" }
    , COMMENT_TEXT { override fun label() = "COMMENT_TEXT" }
    , LOADING_TEXT { override fun label() = "Loading..." }
    , PREV_CLICKABLE
    , PREV_CLICKABLE_TEXT { override fun label() = "Prev" }
    , NEXT_CLICKABLE
    , NEXT_CLICKABLE_TEXT { override fun label() = "Next" } ;

    open fun label() : String = ""
}