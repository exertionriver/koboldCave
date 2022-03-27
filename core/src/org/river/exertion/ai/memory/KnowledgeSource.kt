package org.river.exertion.ai.memory

class KnowledgeSource {

    enum class Source {
        EXPERIENCE
        , LORE
        , LEARNING
        , INTUITION
        , NONE
    }

    var source = Source.NONE
    var trust = 0.5f
}