package org.river.exertion.ai.symbol

enum class LogicType {
    AND { override fun tag() = "and" },
    NONE { override fun tag() = "none" }
    ;
    abstract fun tag() : String
}