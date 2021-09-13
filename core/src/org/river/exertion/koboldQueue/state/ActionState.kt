package org.river.exertion.koboldQueue.state

class ActionState(override val state: String, override val ordering: Int) : Comparable<ActionState>, State(state, ordering) {

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
