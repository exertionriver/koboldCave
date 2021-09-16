package org.river.exertion.ecs.template

import org.river.exertion.koboldQueue.action.actions.Destantiate
import org.river.exertion.koboldQueue.action.actions.Instantiate
import org.river.exertion.koboldQueue.action.roles.IInstantiable
import org.river.exertion.koboldQueue.action.roles.IInstantiator
import org.river.exertion.koboldQueue.time.Timer
import org.river.exertion.koboldQueue.action.roles.IObservable
import org.river.exertion.koboldQueue.condition.SimpleCondition
import org.river.exertion.koboldQueue.condition.SimpleCondition.Always
import org.river.exertion.koboldQueue.condition.SimpleCondition.Eq
import org.river.exertion.koboldQueue.condition.SimpleCondition.Lte
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.river.exertion.ActionConditionsMap
import org.river.exertion.RegisterEntries
import org.river.exertion.koboldQueue.action.ActionPlex
import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.condition.ProbabilitySelect
import org.river.exertion.koboldQueue.time.Moment
import java.util.*
import kotlin.random.Random
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalUnsignedTypes
class Cave(private val id : UUID = UUID.randomUUID(), private val kInstanceName: String) : IInstance, IObservable {

    var momentCounter = 0

    lateinit var entries : RegisterEntries


    override suspend fun perform(timer : Timer, instanceRegister : Register) : Timer = coroutineScope {

        val checkTimer = Timer()

   //     if (timer.getMillisecondsElapsed() / moment.milliseconds > momentCounter) {

//            println("Cave $kInstanceName perform @ ${ DateTime.now() } RT:${timer.getMillisecondsElapsed()} $momentCounter")

        var numKobolds = 0

        lateinit var kobolds : List<IInstance>

        instanceRegister.getNumKobolds().collect{ value -> numKobolds = value }

        instanceRegister.getKobolds().collect{ value -> kobolds = value }

        momentCounter = (timer.getMillisecondsElapsed() / getMoment().milliseconds).toInt()

            if (momentCounter % 5 == 0) {

            //todo : another list for actions that take two slots
            if (actionPlex.slotsInUse() < getMaxPlexSize()) {

                val extendedAction = if (numKobolds > 0)
                    if (numKobolds > 8)
                        ProbabilitySelect(mapOf(
                            Instantiate to Probability(0,0)
                            , Destantiate to Probability(100,0)
                        )).getSelectedProbability()!!
                    else
                        ProbabilitySelect(mapOf(
                            Instantiate to Probability(60,0)
                            , Destantiate to Probability(40,0)
                        )).getSelectedProbability()!!
                else
                    ProbabilitySelect(mapOf(
                        Instantiate to Probability(100,0)
                        , Destantiate to Probability(0,0)
                    )).getSelectedProbability()!!

                val actionParamList = when (extendedAction) {
                    Instantiate -> Instantiate.params { template = Kobold; kInstanceName = "krakka${Random.nextInt(256)}"; register = instanceRegister }
                    Destantiate -> Destantiate.params { kInstance = kobolds[Random.nextInt(numKobolds)]; register = instanceRegister }
                    else -> TODO("something else")
                }

                val conditionParamList = when (extendedAction) {
                    Instantiate -> SimpleCondition.fparams { first = instanceRegister.getNumKobolds(); second = flow { emit(3) } }
                    Destantiate -> null
                    else -> TODO("something else")
                }

                val extendedCondition = when (extendedAction) {
                    Instantiate -> Lte
                    Destantiate -> Always
                    else -> TODO("something else")
                }

                actionPlex.initAction(extendedAction, extendedAction.actionPriority, actionParamList, extendedCondition, conditionParamList)
//                println("extended action started: ${extendedAction.action} by $kInstanceName at $timer" )
            }

//                actionPlex = withContext(RenderActionPlex.getCoroutineContext()) { ActionPlex.perform(actionPlex) }

//                RenderActionPlex.render(id, getMoment(), actionPlex.getEntriesDisplaySortedMap())
                delay(getMoment().milliseconds - checkTimer.getMillisecondsElapsed())

//                println("Cave $kInstanceName checktimer after: ${checkTimer.getMillisecondsElapsed()} ${getMoment().milliseconds}")


      //      println("Cave $kInstanceName checktimer: ${checkTimer.getMillisecondsElapsed()} $momentCounter")
            }

//        delay(GlobalTimer.mSecRenderDelay)

            return@coroutineScope Timer()

 //       } //else

  //      println("Kobold $kInstanceName checktimer: ${checkTimer.getMillisecondsElapsed()} $momentCounter")

   //     return@coroutineScope timer
    }

    override var interrupted = false

    override fun getDescription(): String = "spooky cave!"

    override var actionPlex = ActionPlex(getInstanceId(), getMoment(), getMaxPlexSize())

    override fun getMaxPlexSize() = 1

    override fun getMoment() = momentDuration

    override fun getTemplate() = Companion

    override fun getInstanceId() = id

    override fun getInstanceName() = kInstanceName

    companion object : IInstantiable, IInstantiator {

        override fun getInstance(kInstanceName: String) = Cave(kInstanceName = kInstanceName)

        override fun getTemplateName() : String = Cave::class.simpleName!!

        val momentDuration = Moment(500*4)

        @InternalCoroutinesApi
        @ExperimentalCoroutinesApi
        override val actions: ActionConditionsMap
            get() = modOrSrcXorMap(
                super.actions,
                modMap = mapOf(Instantiate to listOf(Eq))
            )

    }

}