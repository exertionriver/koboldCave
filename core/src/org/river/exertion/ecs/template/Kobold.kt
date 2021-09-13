package org.river.exertion.ecs.template

import kotlinx.coroutines.*
import org.river.exertion.koboldQueue.time.Timer
import org.river.exertion.koboldQueue.action.roles.IInstantiable
import org.river.exertion.koboldQueue.action.roles.IObservable
import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.condition.ProbabilitySelect
import org.river.exertion.koboldQueue.condition.SimpleCondition.Always
import kotlinx.coroutines.flow.collect
import org.river.exertion.ActionConditionsMap
import org.river.exertion.koboldQueue.action.Action
import org.river.exertion.koboldQueue.action.ActionPlex
import org.river.exertion.koboldQueue.action.ActionPriority
import org.river.exertion.koboldQueue.action.IAction
import org.river.exertion.koboldQueue.action.actions.*
import org.river.exertion.koboldQueue.time.Moment
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalUnsignedTypes
@ExperimentalCoroutinesApi
class Kobold(private val id : UUID = UUID.randomUUID(), private val kInstanceName : String) : IInstance, IObservable {

    override fun getDescription(): String = ProbabilitySelect(mapOf(
        "ugly Kobold!" to Probability(40)
        ,"toothy Kobold!" to Probability(30)
        ,"scaly Kobold!" to Probability(30)
    )).getSelectedProbability()!!

    var momentCounter = 0

    @ExperimentalCoroutinesApi
    override suspend fun perform(timer : Timer, instanceRegister : Register) : Timer = coroutineScope {

        val checkTimer = Timer()

//        if (timer.getMillisecondsElapsed() / moment.milliseconds > momentCounter) {

//            println("Kobold $kInstanceName perform @ ${ DateTime.now() } RT:${timer.getMillisecondsElapsed()}, $momentCounter")

        lateinit var updRegister : Register

        instanceRegister.getRegister().collect{ value -> updRegister = value }

            momentCounter = (timer.getMillisecondsElapsed() / getMoment().milliseconds).toInt()

            Companion.baseActions.forEach {
                if (!actionPlex.isBaseActionRunning(it.key) ) {
                    when (it.key) {
                        Look -> actionPlex.initAction(it.key, ActionPriority.BaseAction,  Look.params { kInstance = this@Kobold; register = instanceRegister } )
                        Reflect -> actionPlex.initAction(it.key, ActionPriority.BaseAction, Reflect.params { kInstance = this@Kobold } )
                        else -> actionPlex.initAction(it.key, ActionPriority.BaseAction)
                    }
//                    println("base action started: ${it.key.action} by $kInstanceName at $registerTimer" )
                }
            }

            //todo : another list for actions that take two slots
            if (actionPlex.slotsInUse() < getMaxPlexSize()) {

                val extendedAction = ProbabilitySelect<Action>(mapOf(
                    Idle to Probability(60, 0)
                    , Look to Probability(15,0)
                    , Watch to Probability(15,0)
                    , Screech to Probability(10,0)
                )).getSelectedProbability()!!

                val actionParamList = when (extendedAction) {
                    Look -> Look.params { kInstance = this@Kobold; register = updRegister }
                    Watch -> Watch.params { kInstance = this@Kobold; register = updRegister }
                    Screech -> Screech.params { kInstance = this@Kobold; register = updRegister }
                    else -> Idle.params { kInstance = this@Kobold; moments = Probability(3,2).getValue().toInt() }
                }

                actionPlex.initAction(extendedAction, extendedAction.actionPriority, actionParamList)

//                println("extended action started: ${extendedAction.action} by $kInstanceName at $registerTimer" )
            }

//        launch { actionPlex.perform() }

        if (interrupted) {
//            println("Kobold interrupted! ${getInstanceName()}")
//            actionPlex = withContext(RenderActionPlex.getCoroutineContext()) { ActionPlex.interrupt(actionPlex, getMaxPlexSize()) }
//            RenderActionPlex.render(id, getMoment(), actionPlex.getEntriesDisplaySortedMap(), interrupted)

            interrupted = false
        } else {
//            actionPlex = withContext(RenderActionPlex.getCoroutineContext()) { ActionPlex.perform(actionPlex) }
//            RenderActionPlex.render(id, getMoment(), actionPlex.getEntriesDisplaySortedMap())
        }


//        println("Kobold $kInstanceName checktimer before: ${checkTimer.getMillisecondsElapsed()} $momentCounter")
        delay(getMoment().milliseconds - checkTimer.getMillisecondsElapsed())

//        println("Kobold $kInstanceName checktimer after: ${checkTimer.getMillisecondsElapsed()} ${getMoment().milliseconds}")

//        delay(GlobalTimer.mSecRenderDelay)

        return@coroutineScope Timer()
     //   } //else delay(GlobalTimer.mSecRenderDelay)

     //   return@coroutineScope timer
    }

    override var interrupted = false

    override var actionPlex = ActionPlex(getInstanceId(), getMoment(), getMaxPlexSize())

    override fun getMaxPlexSize() = 5

    override fun getMoment() = Moment(momentDuration.getValue().toLong())

    override fun getTemplate() = Companion

    override fun getInstanceId() = id

    override fun getInstanceName() = kInstanceName

    companion object : IInstantiable, IAction {

        override fun getTemplateName() : String = Kobold::class.simpleName!!

        override fun getInstance(kInstanceName: String) = Kobold(kInstanceName = kInstanceName)

        val momentDuration = Probability(800, 100) //milliseconds

        override val actions: ActionConditionsMap
            get() = super.actions

        override val baseActions: ActionConditionsMap
            get() = mapOf(Look to listOf(Always), Reflect to listOf(Always))
    }
}