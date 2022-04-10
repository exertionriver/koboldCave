package org.river.exertion.ai.property

enum class PropertyType {

    NONE { override fun tag() = "none" }
    ;
    abstract fun tag() : String
}