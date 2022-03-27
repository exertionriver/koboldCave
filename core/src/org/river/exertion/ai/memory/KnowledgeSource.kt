package org.river.exertion.ai.memory

class KnowledgeSource(var source : KnowledgeSource.SourceEnum = SourceEnum.NONE) {

    enum class SourceEnum {
        EXPERIENCE
        , LORE
        , LEARNING
        , INTUITION
        , NONE
    }

    var trust = 0.5f
}