package org.river.exertion.ai.noumena

enum class NoumenonType {

    OTHER { override fun tag() = "other" },
    BEING { override fun tag() = "being" },
    HUMANOID { override fun tag() = "humanoid" },
    LOW_RACE { override fun tag() = "low race" },
    KOBOLD { override fun tag() = "kobold" },

    GROUP { override fun tag() = "group" },
    RED_HAND { override fun tag() = "red hand" },

    INDIVIDUAL { override fun tag() = "individual" },
    NONE { override fun tag() = "none" }
    ;
    abstract fun tag() : String
}