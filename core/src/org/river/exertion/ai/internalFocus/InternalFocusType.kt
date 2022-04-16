package org.river.exertion.ai.internalFocus

enum class InternalFocusType {

        QUEST { override fun tag() = "quest" }, //ongoing set of objectives, no resolution found
            // Symbol Instance (Belief) + Set of Symbol Instances (Vision)

        MISSION { override fun tag() = "mission" }, //sequence of objectives, resolution found
            //sequence of symbols / conditions (symbol instance + list of symbol instances)
        STRATEGY { override fun tag() = "strategy" }, //approach to achieving an objective or respond to a symbol, proposed approach to realize objective, posture
            //e.g. BEFRIEND (symbol instance + set of symbol instances)
        TACTIC { override fun tag() = "tactic" }, //action or device for accomplishing a strategy, impact-improver for realizing objective
            //e.g. OFFER_SOMETHING (symbol instance + set of symbol instances)
    NONE
    ;
    open fun tag() : String = "none"
}