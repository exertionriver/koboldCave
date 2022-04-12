package org.river.exertion.ai.symbol

enum class MarkType {

    HARMFUL { override fun tag() = "harmful" },
    NONE { override fun tag() = "none" }
    ;
    abstract fun tag() : String
}