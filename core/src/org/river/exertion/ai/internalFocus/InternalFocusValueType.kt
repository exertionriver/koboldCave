package org.river.exertion.ai.internalFocus

enum class InternalFocusValueType {

    INDIVIDUAL_SURVIVAL { override fun tag() = "survival" },
    NONE
    ;
    open fun tag() : String = "none"
}