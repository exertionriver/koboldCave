package koboldQueue

import KGdxTestRunner
import com.badlogic.ashley.core.PooledEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.river.exertion.ecs.component.ActionLookComponent
import org.river.exertion.ecs.component.KoboldComponent
import org.river.exertion.ecs.system.ActionLookSystem
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
//@RunWith(KGdxTestRunner::class)
class TestKoboldActions {

    val engine = PooledEngine()

    val kobold = engine.entity {
        with<KoboldComponent>()
    }.apply { this[KoboldComponent.mapper]?.instantiate("gragga") }

    val koboldSecond = engine.entity {
        with<KoboldComponent>()
    }.apply { this[KoboldComponent.mapper]?.instantiate("krakka") }

    val koboldThird = engine.entity {
        with<KoboldComponent>()
    }.apply { this[KoboldComponent.mapper]?.instantiate("razza")  }

    @Test
    fun testActionEnumeration() {

        kobold[KoboldComponent.mapper]?.baseActions!!.forEach{ println("base action: $it") }
        kobold[KoboldComponent.mapper]?.extendedActions!!.forEach{ println("extended action: $it") }

        kobold.components.forEach { println("$it") }
    }

    @Test
    fun testActionLook() {
        kobold.add(ActionLookComponent())

        (0..3).forEach { runBlocking { delay(500) } ; println("pre:waited .5s!"); engine.update(.5f) }

        engine.apply {
            addSystem(ActionLookSystem())
        }

        koboldSecond.add(ActionLookComponent())

        (0..10).forEach { runBlocking { delay(500) } ; println("waited .5s!"); engine.update(.5f) }

        koboldThird.add(ActionLookComponent())

        engine.removeEntity(koboldSecond)

        (0..3).forEach { runBlocking { delay(500) } ; println("post:waited .5s!"); engine.update(.5f) }

        engine.removeEntity(koboldThird)

        (0..3).forEach { runBlocking { delay(500) } ; println("post2:waited .5s!"); engine.update(.5f) }
    }

    @Test
    fun testActionPlex() {
        (0..3).forEach { runBlocking { delay(500) } ; println("pre:waited .5s!"); engine.update(.5f) }

        engine.apply {
            addSystem(ActionLookSystem())
        }

        (0..10).forEach { runBlocking { delay(500) } ; println("waited .5s!"); engine.update(.5f) }

        engine.removeEntity(koboldSecond)

        (0..3).forEach { runBlocking { delay(500) } ; println("post:waited .5s!"); engine.update(.5f) }

        engine.removeEntity(koboldThird)

        (0..3).forEach { runBlocking { delay(500) } ; println("post2:waited .5s!"); engine.update(.5f) }
    }


}