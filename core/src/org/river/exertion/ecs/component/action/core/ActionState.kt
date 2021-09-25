package org.river.exertion.ecs.component.action.core

class ActionState(val state: String, val ordering: Int) : Comparable<ActionState> {

    companion object {
        val ActionQueue = ActionState("actionQueue", 1)
        val ActionPrepare = ActionState("actionPrepare", 2)
        val ActionExecute = ActionState("actionExecute", 3)
        val ActionRecover = ActionState("actionRecover", 4)
        val ActionStateNone = ActionState("actionStateNone", 0)

        val InProcess = listOf(ActionPrepare, ActionExecute, ActionRecover)
        val Interruptable = listOf(ActionPrepare, ActionExecute, ActionRecover)
        val Preemptable = listOf(ActionPrepare)
    }

    override fun toString() = "${ActionState::class.simpleName}($state)"

    override fun compareTo(other: ActionState): Int = this.ordering.compareTo(other.ordering)

}
