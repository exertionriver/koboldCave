package org.river.exertion.ecs.component.action.core

enum class ActionType {

    INSTANTIATE { override fun tag() = "instantiate" },
    DESTANTIATE { override fun tag() = "destantiate" },
    IDLE { override fun tag() = "idle" },
    SLEEP { override fun tag() = "sleep" },
    LOOK { override fun tag() = "look" },
    MOVE { override fun tag() = "move" },
    REFLECT { override fun tag() = "reflect" },
    SCREECH { override fun tag() = "screech" },
    WATCH { override fun tag() = "watch" },
    ABIDE { override fun tag() = "abide" },
    APPROACH { override fun tag() = "approach" },
    BALTER { override fun tag() = "baltering" },
    SIT { override fun tag() = "sit" },
    STAND { override fun tag() = "stand" },
    LIE_DOWN { override fun tag() = "lie down" },
    THINK { override fun tag() = "think" },
    WANDER { override fun tag() = "wander" },
    NONE { override fun tag() = "none" }
    ;
    abstract fun tag() : String
}