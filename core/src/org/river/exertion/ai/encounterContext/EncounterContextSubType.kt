package org.river.exertion.ai.encounterContext

enum class EncounterContextSubType {
    UNKNOWN { override fun tag() = "" }, //cannot tell
    HOSTILE { override fun tag() = "hostile" },
    INDIFFERENT { override fun tag() = "indifferent" },
    FRIENDLY { override fun tag() = "friendly" },
    NONE
    ;
    open fun tag() : String = "none"
}