package org.river.exertion.ecs.component.action.core

class ActionType(val type: String) {

    override fun toString() = "${ActionType::class.simpleName}($type)"

    companion object {
        val Continual = ActionType("continualExec") //prepares again after recovery
        val OneTimeExec = ActionType("oneTimeExec") //removes itself after recovery
    }
}
