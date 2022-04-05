package org.river.exertion.ai.internalState

enum class InternalFacetType {

    ANGER { override fun tag() = "anger" },
    FEAR { override fun tag() = "fear" },
    NONE { override fun tag() = "none" }
    ;
    abstract fun tag() : String
}