package org.river.exertion.ecs.component.action.core

object ActionNoneComponent : IActionComponent {

    override val label = "None"
    override val description = { "None" }
    override var type = ActionType.OneTimeExec
    override var priority = ActionPriority.MediumSecond
    override var state = ActionState.ActionStateNone

    override var plexSlotsFilled = 0
    override var plexSlotsRequired = 1
    override var maxParallel = 2

    override val momentsToPrepare = 2
    override val momentsToExecute = 3
    override val momentsToRecover = 2
}