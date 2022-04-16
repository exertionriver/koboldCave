package org.river.exertion.ai.encounterContext

//inspired by https://en.wikipedia.org/wiki/Proxemics

enum class EncounterContextType {
    LIMINAL { override fun tag() = "liminal" }, //at the edge of perception; alone
    PERCEPTUAL { override fun tag() = "perceptual" }, //able to perceive attributes, etc.; 'what is it?'
    SOCIAL { override fun tag() = "social" }, //interactive or communicative; attempt to resolve encounter
    SOCIAL_HOSTILE { override fun subType() = EncounterContextSubType.HOSTILE; override fun tag() = "${SOCIAL.tag()} ${subType().tag()}" },
    SOCIAL_INDIFFERENT { override fun subType() = EncounterContextSubType.INDIFFERENT; override fun tag() = "${SOCIAL.tag()} ${subType().tag()}" },
    SOCIAL_FRIENDLY { override fun subType() = EncounterContextSubType.FRIENDLY; override fun tag() = "${SOCIAL.tag()} ${subType().tag()}" },
    FAMILIAR { override fun tag() = "familiar" }, //closely social
    FAMILIAR_ENEMY { override fun subType() = EncounterContextSubType.HOSTILE; override fun tag() = "${FAMILIAR.tag()} enemy" },
    FAMILIAR_INDIFFERENT { override fun subType() = EncounterContextSubType.INDIFFERENT; override fun tag() = "${FAMILIAR.tag()} ${subType().tag()}" },
    FAMILIAR_FRIENDLY { override fun subType() = EncounterContextSubType.FRIENDLY; override fun tag() = "${FAMILIAR.tag()} ${subType().tag()}" },
    INTIMATE { override fun tag() = "intimate" },
    INTIMATE_VIOLENT { override fun subType() = EncounterContextSubType.HOSTILE; override fun tag() = "${INTIMATE.tag()} violent" },
    INTIMATE_DETACHED { override fun subType() = EncounterContextSubType.INDIFFERENT; override fun tag() = "${INTIMATE.tag()} detached" },
    INTIMATE_LOVING { override fun subType() = EncounterContextSubType.FRIENDLY; override fun tag() = "${INTIMATE.tag()} loving" },
    NONE
    ;
    open fun tag() : String = "none"
    open fun subType() : EncounterContextSubType = EncounterContextSubType.UNKNOWN
}