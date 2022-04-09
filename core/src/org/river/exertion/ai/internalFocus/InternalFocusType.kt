package org.river.exertion.ai.internalFocus

enum class InternalFocusType {

    //https://ebrary.net/10994/business_finance/mission_statements

    VISION { override fun tag() = "vision" }, //an overall vision of where an entity should be in, say, 3-5 years should have clear objectives to which strategies can be linked
    MISSION { override fun tag() = "mission" }, //a declaration of core purpose
    VALUE { override fun tag() = "value" }, //principles that guide external and internal conduct
    MOTIVE { override fun tag() = "motive" }, //a reason for doing something
    STRATEGY { override fun tag() = "strategy" }, //approach to achieving an objective
    TACTIC { override fun tag() = "tactic" }, //action or device for accomplishing a strategy
    OBJECTIVE { override fun tag() = "objective" }, //a goal to be achieved
    NONE
    ;
    open fun tag() : String = "none"
}