package org.river.exertion.koboldQueue.action

import org.river.exertion.koboldQueue.action.Action.Companion.ActionNone
import org.river.exertion.koboldQueue.action.ActionPriority.Companion.ActionPriorityNone
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.river.exertion.ParamList
import org.river.exertion.koboldQueue.state.ActionState
import org.river.exertion.koboldQueue.state.ActionState.Companion.ActionStateNone
import org.river.exertion.koboldQueue.time.Timer
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
class StateAction (val action : Action, val plexSlotsFilled : Int = 0, val actionState: ActionState = ActionStateNone, val actionPriority: ActionPriority = ActionPriorityNone, val actionParamList: ParamList? = null, val timer: Timer = Timer()) {

    //update constructor
    constructor(copyStateAction : StateAction
                , updAction : Action = copyStateAction.action
                , updPlexSlotsFilled : Int = copyStateAction.plexSlotsFilled
                , updActionState : ActionState = copyStateAction.actionState
                , updActionPriority : ActionPriority = copyStateAction.actionPriority
                , updActionParamList : ParamList? = copyStateAction.actionParamList
                , updTimer : Timer = copyStateAction.timer
    ) : this (
        action = updAction
        , plexSlotsFilled = updPlexSlotsFilled
        , actionState = updActionState
        , actionPriority = updActionPriority
        , actionParamList = updActionParamList
        , timer = updTimer
    )

    override fun toString() = "${StateAction::class.simpleName}($action, $plexSlotsFilled, $actionState, $actionPriority, $actionParamList, $timer)"

    companion object {

        val StateActionNone = StateAction(ActionNone)
    }
}