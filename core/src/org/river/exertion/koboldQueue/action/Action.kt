package org.river.exertion.koboldQueue.action

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import org.river.exertion.koboldQueue.action.ActionPriority.Companion.MediumSecond
import org.river.exertion.koboldQueue.action.ActionType.Companion.OneTimeExec
import org.river.exertion.ActionDescription
import org.river.exertion.ActionExecutor
import org.river.exertion.ConditionParamMap
import org.river.exertion.ParamList
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
@ExperimentalTime
open class Action(val actionLabel : String, val momentsToPrepare : Int = 2, val momentsToExecute : Int = 3, val momentsToRecover : Int = 2, val plexSlotsRequired : Int = 1, val maxParallel : Int = 2,
                  val actionType : ActionType = OneTimeExec, val actionPriority : ActionPriority = MediumSecond, val description : ActionDescription, val executor : ActionExecutor
) {

    //update constructor
    constructor(copyAction : Action
                , updActionLabel : String = copyAction.actionLabel
                , updMomentsToPrepare : Int = copyAction.momentsToPrepare
                , updMomentsToExecute : Int = copyAction.momentsToExecute
                , updMomentsToRecover : Int = copyAction.momentsToRecover
                , updPlexSlotsRequired: Int = copyAction.plexSlotsRequired
                , updMaxParallel: Int = copyAction.maxParallel
                , updActionType: ActionType = copyAction.actionType
                , updActionPriority: ActionPriority = copyAction.actionPriority
                , updDescription : ActionDescription = copyAction.description
                , updExecutor : ActionExecutor = copyAction.executor
    ) : this (
        actionLabel = updActionLabel
        , momentsToPrepare = updMomentsToPrepare
        , momentsToExecute = updMomentsToExecute
        , momentsToRecover = updMomentsToRecover
        , plexSlotsRequired = updPlexSlotsRequired
        , maxParallel = updMaxParallel
        , actionType = updActionType
        , actionPriority = updActionPriority
        , description = updDescription
        , executor = updExecutor
    )


    object Immediate : IAction {

        @ExperimentalCoroutinesApi
        override suspend fun execute(action: Action, conditionParamMap: ConditionParamMap, actionParamList: ParamList?) = coroutineScope {
            super.execute(action, conditionParamMap, actionParamList)

            return@coroutineScope
        }

    }

    override fun toString() = "${Action::class.simpleName}($actionLabel, $momentsToPrepare, $momentsToExecute, $momentsToRecover, $plexSlotsRequired, $maxParallel, $actionType, $actionPriority, $description, executor())"

    override fun equals(other: Any?): Boolean {
        return this.actionLabel == (other as Action).actionLabel
    }

    override fun hashCode(): Int {
        var result = actionLabel.hashCode()
        result = 31 * result + momentsToPrepare
        result = 31 * result + momentsToExecute
        result = 31 * result + momentsToRecover
        result = 31 * result + plexSlotsRequired
        result = 31 * result + maxParallel
        result = 31 * result + actionType.hashCode()
        result = 31 * result + actionPriority.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + executor.hashCode()
        return result
    }

    companion object {

        val ActionNone = Action(actionLabel = "none", description = fun() : String = "none", executor = fun(_: ParamList?) : String = "none")
    }
}