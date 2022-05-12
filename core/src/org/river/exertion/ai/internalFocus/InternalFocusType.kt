package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.symbol.*

enum class InternalFocusType {

//ground for resolving targets
    //symbol + conviction (ordered by desc are values
    BELIEF { override fun tag() = "belief" },

    // relating symbols to symbols
    LOGIC { override fun tag() = "logic" },

//methods for getting to targets
    //ongoing generator of targets
    QUEST { override fun tag() = "quest" },

    //sequence of objectives, generates one target after another
    MISSION { override fun tag() = "mission" },

    //step towards accomplish mission
    OBJECTIVE { override fun tag() = "objective" },

    //approach to accomplish objective or respond to a symbol
    STRATEGY { override fun tag() = "strategy" },

    //practical step, action or device for accomplishing a strategy, impact-improver for realizing objective
    TACTIC { override fun tag() = "tactic" },

    NONE
    ;
    open fun tag() : String = "none"
}