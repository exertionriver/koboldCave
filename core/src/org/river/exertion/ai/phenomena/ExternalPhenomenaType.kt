package org.river.exertion.ai.phenomena

import org.river.exertion.ai.memory.KnowledgeSourceType

enum class ExternalPhenomenaType {

    VISUAL { override fun perceivedExperienceAction() = "I saw"},
    AUDITORY { override fun perceivedExperienceAction() = "I heard"},
    TACTILE { override fun perceivedExperienceAction() = "I felt"},
    GUSTATORY { override fun perceivedExperienceAction() = "I tasted"},
    OLFACTORY { override fun perceivedExperienceAction() = "I smelled"},
    WISDOM { override fun perceivedExperienceAction() = "It seemed"}, //ie. introspection / states (empathy)
    INTELLIGENCE { override fun perceivedExperienceAction() = "It looked"}, //ie. discerning motive / situation
    EXTRASENSORY { override fun perceivedExperienceAction() = "I thought"}, //esp
    NONE
    ;
    open fun perceivedExperienceAction() = "none"

    fun perceivedAction(knowledgeSourceType: KnowledgeSourceType) : String =
        when (knowledgeSourceType) {
            KnowledgeSourceType.EXPERIENCE -> perceivedExperienceAction()
            KnowledgeSourceType.LEARNING -> "I read that"
            KnowledgeSourceType.LORE -> "I heard that"
            KnowledgeSourceType.INTUITION -> "I could tell that"
            else -> "not sure"
        }
}