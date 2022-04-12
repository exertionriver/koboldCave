package org.river.exertion.ai.internalFocus

enum class InternalFocusType { //works with symbols?
    GROUNDING,
        BELIEF { override fun tag() = "belief" }, //e.g. Kobolds are annoying -- Symbol + Noumenon?
            //'is' / 'have' statements about others, linking symbols with behaviors, conditions, noumenon
        VALUE { override fun tag() = "value" }, //used to rank and order
            //symbols ranking
        LOGIC { override fun tag() = "logic"}, //structured focuses
            //
        MOTIVE { override fun tag() = "motive" }, //a reason for doing something
            //
    PATHING,
        QUEST { override fun tag() = "quest" }, //endeavors to fulfill a request
            //sequence of symbols / conditions
        MISSION { override fun tag() = "mission" }, //long-term, endeavors to fulfill a vision
            //sequence of symbols / conditions
        STRATEGY { override fun tag() = "strategy" }, //approach to achieving an objective
            //e.g. BEFRIEND
        TACTIC { override fun tag() = "tactic" }, //action or device for accomplishing a strategy
            //e.g. OFFER_SOMETHING
    TARGETING,
        VISION { override fun tag() = "vision" }, //long-term target
        NEED { override fun tag() = "need" },
            //e.g. FOOD
        WANT { override fun tag() = "want" },
            //e.g. SHINY_THING
        OBJECTIVE { override fun tag() = "objective" }, //short-term target
            //e.g.
        GOAL { override fun tag() = "goal" }, //time-bound, specific, measureable target
    NONE
    ;
    open fun tag() : String = "none"
}