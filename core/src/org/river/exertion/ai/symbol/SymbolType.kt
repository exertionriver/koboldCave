package org.river.exertion.ai.symbol

enum class SymbolType {

    THREAT { override fun tag() = "threat" },
    OPPORTUNITY { override fun tag() = "opportunity" },
    ANNOYANCE { override fun tag() = "annoyance" },
    KINSHIP { override fun tag() = "annoyance" },
    NONE { override fun tag() = "none" }
    ;
    abstract fun tag() : String
}