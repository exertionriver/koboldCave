package render

enum class CommandView {
    LABEL_TEXT { override fun label() = "LABEL_TEXT" }
    , DESCRIPTION_TEXT { override fun label() = "DESCRIPTION_TEXT" }
    , COMMENT_TEXT { override fun label() = "COMMENT_TEXT" }
    , LOADING_TEXT { override fun label() = "Loading..." }
    , PREV_BUTTON
    , NEXT_BUTTON
    , NODE_UUID_TEXT { override fun label() = "Click a node for Uuid" }
    , NODE_DESCRIPTION_TEXT { override fun label() = "Click a node for Description"}
    ;

    open fun label() : String = ""
}