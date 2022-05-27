package org.river.exertion.ai.messaging

enum class MessageChannel {

    ECS_S2D_BRIDGE,
    S2D_ECS_BRIDGE,
    ECS_FSM_BRIDGE,
    PLAN_BRIDGE,
    PERCEPTION_BRIDGE,
    FEELING_BRIDGE,
    CURNODE_BRIDGE,
    NODEROOMMESH_BRIDGE,
    LOSMAP_BRIDGE,
    EXT_PHENOMENA,
    INT_PHENOMENA,
    INT_CONDITION,
    INT_FACET,
    INT_MEMORY,
    INT_SYMBOL,
    INT_FOCUS,
    INT_PHENOMENA_FACETS,
    INT_MEMORY_FACETS
    ;

    fun id() = this.ordinal
}