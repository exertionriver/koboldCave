package org.river.exertion.ai.internalState

enum class InternalFacetType {

    ANGER { override fun tag() = "anger"; override fun description() = "angry" },
    FEAR { override fun tag() = "fear"; override fun description() = "fearful" },
    NONE
    ;
    open fun tag() : String = "none"
    open fun description() : String = "nothing much"
}