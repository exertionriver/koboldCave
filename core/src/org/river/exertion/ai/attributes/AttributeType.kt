package org.river.exertion.ai.attributes

enum class AttributeType {

    GROWL { override fun tag() = "growl" },
    INTELLIGENCE { override fun tag() = "intelligence" },
    INTERNAL_STATE { override fun tag() = "internal state" },
    NONE { override fun tag() = "none" }
    ;
    abstract fun tag() : String
}