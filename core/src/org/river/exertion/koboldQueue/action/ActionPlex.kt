package org.river.exertion.koboldQueue.action

import org.river.exertion.koboldQueue.action.actions.Idle
import org.river.exertion.koboldQueue.condition.Condition
import org.river.exertion.koboldQueue.condition.SimpleCondition.Always
import org.river.exertion.koboldQueue.condition.StateCondition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import org.river.exertion.ParamList
import org.river.exertion.koboldQueue.state.ActionState
import org.river.exertion.koboldQueue.time.Moment
import org.river.exertion.koboldQueue.time.Timer
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalUnsignedTypes
@ExperimentalCoroutinesApi
class ActionPlex(val instanceID : UUID, val moment : Moment, val maxPlexSize : Int) {

    val actionEntries : MutableMap<UUID, StateAction> = mutableMapOf() //slots to StateActions, max of maxPlexSize
    val conditionEntries : MutableMap<UUID, StateCondition> = mutableMapOf() //conditions for respective actions

    fun getEntriesDisplaySortedMap() = actionEntries.toList().sortedWith (compareBy<Pair<UUID, StateAction>> { it.second.actionPriority }.thenByDescending { it.second.actionState }
        .thenByDescending { it.second.timer.getMillisecondsElapsed() }).toMap()

    fun getEntriesPerformSortedMap() = actionEntries.toList().sortedWith (compareBy<Pair<UUID, StateAction>> { it.second.actionPriority }.thenByDescending { it.second.timer.getMillisecondsElapsed() })

    fun slotsInUse() : Int {

        val inProcessActions = actionEntries.filterValues { plexAction -> ActionState.InProcess.contains(plexAction.actionState) }

        return if (inProcessActions.isNullOrEmpty()) 0 else inProcessActions.map{ plexAction -> plexAction.value.plexSlotsFilled }.reduce{ slotsInUse : Int, plexActionSlotsFilled -> slotsInUse + plexActionSlotsFilled }
    }

    fun slotsAvailable() : Int {

        return maxPlexSize - slotsInUse()
    }

    fun momentsPassed(stateActionUuid : UUID) : Int = (getActionTimer(stateActionUuid).getMillisecondsElapsed() / moment.milliseconds).toInt()

    fun isActionQueued(stateActionUuid : UUID) = (getActionState(stateActionUuid) == ActionState.ActionQueue)

    fun isActionPrepared(stateActionUuid : UUID) = (getActionState(stateActionUuid) == ActionState.ActionPrepare) &&
            (momentsPassed(stateActionUuid) >= actionEntries[stateActionUuid]!!.action.momentsToPrepare)

    fun isActionExecuted(stateActionUuid : UUID) = (getActionState(stateActionUuid) == ActionState.ActionExecute) &&
            (momentsPassed(stateActionUuid) >= actionEntries[stateActionUuid]!!.action.momentsToExecute)

    fun isActionRecovered(stateActionUuid : UUID) = (getActionState(stateActionUuid) == ActionState.ActionRecover) &&
            (momentsPassed(stateActionUuid) >= actionEntries[stateActionUuid]!!.action.momentsToRecover)

    fun isBaseActionRunning(action: Action) : Boolean =
        actionEntries.filterValues { stateAction -> stateAction.action == action && stateAction.actionPriority == ActionPriority.BaseAction }.isNotEmpty()

    fun numActionsRunning(action: Action) : Int =
        actionEntries.filterValues { stateAction -> stateAction.action == action }.keys.size

    fun getStateAction(stateActionUuid: UUID) : StateAction = if (actionEntries[stateActionUuid] != null) actionEntries[stateActionUuid]!! else StateAction.StateActionNone

    fun getAction(stateActionUuid: UUID) : Action = if (actionEntries[stateActionUuid] != null) actionEntries[stateActionUuid]!!.action else Action.ActionNone

    fun getActionType(stateActionUuid: UUID) : ActionType = if (actionEntries[stateActionUuid] != null) getAction(stateActionUuid).actionType else ActionType.OneTimeExec

    fun getActionState(stateActionUuid: UUID) : ActionState = if (actionEntries[stateActionUuid] != null) actionEntries[stateActionUuid]!!.actionState else ActionState.ActionStateNone

    fun getActionPriority(stateActionUuid: UUID) : ActionPriority = if (actionEntries[stateActionUuid] != null) actionEntries[stateActionUuid]!!.actionPriority else ActionPriority.ActionPriorityNone

    fun getActionParamList(stateActionUuid: UUID) : ParamList? = if (actionEntries[stateActionUuid] != null) actionEntries[stateActionUuid]!!.actionParamList else null

    fun getActionTimer(stateActionUuid: UUID) : Timer = if (actionEntries[stateActionUuid] != null) actionEntries[stateActionUuid]!!.timer else Timer()

    fun getCondition(stateActionUuid: UUID) : Condition = if (conditionEntries[stateActionUuid] != null) conditionEntries[stateActionUuid]!!.condition else Condition.ConditionNone

    fun getConditionParamList(stateActionUuid: UUID) : ParamList? = if (conditionEntries[stateActionUuid] != null) conditionEntries[stateActionUuid]!!.conditionParamList else null

    fun cycleState(stateActionUuid : UUID) {
        when (getActionState(stateActionUuid) ) {
            ActionState.ActionQueue -> actionEntries[stateActionUuid] = StateAction(copyStateAction = actionEntries[stateActionUuid]!!, updActionState = ActionState.ActionPrepare, updTimer = Timer())
            ActionState.ActionPrepare -> actionEntries[stateActionUuid] = StateAction(copyStateAction = actionEntries[stateActionUuid]!!, updActionState = ActionState.ActionExecute, updTimer = Timer())
            ActionState.ActionExecute -> actionEntries[stateActionUuid] = StateAction(copyStateAction = actionEntries[stateActionUuid]!!, updActionState = ActionState.ActionRecover, updTimer = Timer())
            ActionState.ActionRecover -> actionEntries[stateActionUuid] = StateAction(copyStateAction = actionEntries[stateActionUuid]!!, updActionState = ActionState.ActionQueue, updTimer = Timer())
            else -> actionEntries[stateActionUuid] = StateAction(copyStateAction = actionEntries[stateActionUuid]!!, updActionState = ActionState.ActionQueue)
        }
    }

    fun initAction(action: Action, actionPriority: ActionPriority, actionParamList : ParamList? = null, condition : Condition? = Always, conditionParamList : ParamList? = null) {
        if (numActionsRunning(action) < action.maxParallel)  {

            val newPlexActionUuid = UUID.randomUUID()
            when (actionPriority) {
                ActionPriority.BaseAction -> actionEntries[newPlexActionUuid] = StateAction(
                    Action(copyAction = action, updActionType = ActionType.Continual), action.plexSlotsRequired,
                    ActionState.ActionQueue, actionPriority, actionParamList)
                else -> actionEntries[newPlexActionUuid] = StateAction(action, action.plexSlotsRequired, ActionState.ActionQueue, actionPriority, actionParamList)
            }

            if (condition != null) {
                conditionEntries[newPlexActionUuid] = StateCondition(condition, conditionParamList)
            }
        } //else println ("maxParallel reached for ${action.actionLabel}")
    }

    @ExperimentalUnsignedTypes
    fun cancelAction(stateActionUuid: UUID) = actionEntries.remove(stateActionUuid)

    @ExperimentalUnsignedTypes
    fun cancelAll() = actionEntries.clear()

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    fun queueAction(stateActionUuid : UUID) {
        when (getActionState(stateActionUuid)) {
            ActionState.ActionRecover -> if (getActionType(stateActionUuid) == ActionType.Continual) cycleState(stateActionUuid) else cancelAction(stateActionUuid)
            else -> cycleState(stateActionUuid)
        }
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    fun prepareAction(stateActionUuid : UUID) {
        when (getActionState(stateActionUuid)) {
            ActionState.ActionQueue -> {
                if ( preempt(stateActionUuid) ) cycleState(stateActionUuid)
                else if ( getActionPriority(stateActionUuid) == ActionPriority.BaseAction && interrupt(actionEntries[stateActionUuid]!!.action.plexSlotsRequired) ) cycleState(stateActionUuid)
            }
            else -> cycleState(stateActionUuid)
        }
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    suspend fun executeAction(stateActionUuid : UUID) {
        when (getActionState(stateActionUuid)) {
            ActionState.ActionPrepare -> {
                cycleState(stateActionUuid)

//                println("actionExec: ${getAction(stateActionUuid)}, ${getCondition(stateActionUuid)}, ${getConditionParamList(stateActionUuid)}")

                Action.Immediate.execute(
                    getAction(stateActionUuid),
                    mapOf(Pair(getCondition(stateActionUuid), getConditionParamList(stateActionUuid))),
                    getActionParamList(stateActionUuid)
                )

                //extend execution time by idle moments param
                if ( getAction(stateActionUuid) == Idle) {
                    actionEntries[stateActionUuid] = StateAction(copyStateAction = getStateAction(stateActionUuid)
                        , updAction = Action(getAction(stateActionUuid), updMomentsToExecute = Idle.IdleParamList(getActionParamList(stateActionUuid)!!).moments!! ) )
                }

            }
            else -> cycleState(stateActionUuid)
        }
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    fun recoverAction(stateActionUuid : UUID) {
        when (getActionState(stateActionUuid)) {
            else -> cycleState(stateActionUuid) //cycle state by default
        }
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    fun interruptAction(stateActionUuid : UUID) {
        when (getActionState(stateActionUuid)) {
            ActionState.ActionExecute -> queueAction(stateActionUuid) //assess if there is penalty
            ActionState.ActionRecover -> actionEntries[stateActionUuid] = StateAction(copyStateAction = getStateAction(stateActionUuid), updActionState = ActionState.ActionRecover, updTimer = Timer())    //restart recover
            else -> queueAction(stateActionUuid) //no penalty
        }
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    fun preemptAction(stateActionUuid : UUID) {
        actionEntries[stateActionUuid] = StateAction(copyStateAction = getStateAction(stateActionUuid), updActionState = ActionState.ActionQueue, updTimer = Timer())
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ExperimentalUnsignedTypes
    fun interrupt(slotsToInterrupt : Int) : Boolean {

//    println("interrupt($slotsToInterrupt, $maxPlexSize)")

        var filledSlotsToInterrupt = slotsToInterrupt - slotsAvailable()

        if (filledSlotsToInterrupt <= 0 ) return true

        val interruptables = actionEntries.filterValues { ActionState.Interruptable.contains(it.actionState) }.toList().sortedWith (compareByDescending <Pair<UUID, StateAction>> { it.second.actionPriority }.thenBy { it.second.timer.getMillisecondsElapsed() })

//    println("interruptables(${interruptables.size}):")

//    interruptables.forEach { println("interruptable: $it") }

        val interruptableFilledSlots = if (interruptables.isNullOrEmpty()) 0 else interruptables.map { it.second.plexSlotsFilled }.reduce{ result : Int, element -> result + element }

        if (filledSlotsToInterrupt <= interruptableFilledSlots) {
            for (interruptable in interruptables) {
                if (filledSlotsToInterrupt <= 0) return true

  //          println("${interruptable.first} INTERRUPTED!")
                interruptAction(interruptable.first)
                filledSlotsToInterrupt -= actionEntries[interruptable.first]!!.plexSlotsFilled
            }
        }
        return false
    }

    fun preempt(stateActionUuid: UUID) : Boolean {

//    println("preempt($actionPriority, $slotsToPreempt, $maxPlexSize)")

        var filledSlotsToPreempt = getStateAction(stateActionUuid).action.plexSlotsRequired - slotsAvailable()

//    println("filled slots to preempt:${filledSlotsToPreempt}")

        if (filledSlotsToPreempt <= 0) return true

        val preemptables = actionEntries.filterValues {
            ActionState.Preemptable.contains(it.actionState) && it.actionPriority > getActionPriority(stateActionUuid)
        }.toList().sortedWith (compareByDescending <Pair<UUID, StateAction>> { it.second.actionPriority }.thenBy { it.second.timer.getMillisecondsElapsed() })

//    println("preemptables(${preemptables.size}):")

//    preemptables.forEach { println("preemptable: $it") }

        val preemptableFilledSlots = if (preemptables.isNullOrEmpty()) 0 else preemptables.map { it.second.plexSlotsFilled }.reduce{ result : Int, element -> result + element }

        if (filledSlotsToPreempt <= preemptableFilledSlots) {
            for (preemptable in preemptables) {
                if (filledSlotsToPreempt <= 0) return true

  //          println("${preemptable.first} PREEMPTED!" )
                preemptAction(preemptable.first)
                filledSlotsToPreempt -= actionEntries[preemptable.first]!!.plexSlotsFilled
            }
        }

        return false
    }

    fun stateString() : List<String> {

        val returnState = mutableListOf<String>()

        returnState.add("slots in use: ${slotsInUse()}")

        actionEntries.toList().sortedWith (compareBy<Pair<UUID, StateAction>> { it.second.actionPriority }.thenByDescending { it.second.timer.getMillisecondsElapsed() }).forEach{ returnState.add("${it.first}: ${it.second}") }

        return returnState
    }

    companion object {

        suspend fun perform(actionPlex: ActionPlex) : ActionPlex = coroutineScope {

            //          val checkTimer = Timer()

            actionPlex.getEntriesPerformSortedMap().forEach{
                //        println(it.first)
                when {
                    actionPlex.isActionQueued(it.first) -> actionPlex.prepareAction(it.first)
                    actionPlex.isActionPrepared(it.first) -> actionPlex.executeAction(it.first)
                    actionPlex.isActionExecuted(it.first) -> actionPlex.recoverAction(it.first)
                    actionPlex.isActionRecovered(it.first) -> actionPlex.queueAction(it.first)
                }
            }

//            println("ActionPlex checktimer: ${checkTimer.getMillisecondsElapsed()}")

            return@coroutineScope actionPlex
        }

        fun interrupt(actionPlex: ActionPlex, numSlotsToInterrupt : Int) : ActionPlex {

            actionPlex.interrupt(numSlotsToInterrupt)
//            println("ActionPlex checktimer: ${checkTimer.getMillisecondsElapsed()}")

            return actionPlex
        }

    }
}