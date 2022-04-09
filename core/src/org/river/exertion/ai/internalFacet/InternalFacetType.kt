package org.river.exertion.ai.internalFacet

enum class InternalFacetType {

    ANGER { override fun tag() = "anger"; override fun description() = "angry" },
    FEAR { override fun tag() = "fear"; override fun description() = "fearful" },
    SURPRISE { override fun tag() = "surprise"; override fun description() = "surprised" },
    CONFUSION { override fun tag() = "confusion"; override fun description() = "confused" },
    DESIRE { override fun tag() = "desire"; override fun description() = "desirous" },
    DISDAIN { override fun tag() = "disdain"; override fun description() = "disdainful" },
    DISGUST { override fun tag() = "disgust"; override fun description() = "disgusted" },
    DOUBT { override fun tag() = "doubt"; override fun description() = "doubtful" },
    NONE
    ;
    open fun tag() : String = "none"
    open fun description() : String = "nothing much"
}