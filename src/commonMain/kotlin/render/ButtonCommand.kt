package render

enum class ButtonCommand {
    PREV { override fun label() = "Prev" }
    , NEXT { override fun label() = "Next" }
    ;

    abstract fun label() : String
}