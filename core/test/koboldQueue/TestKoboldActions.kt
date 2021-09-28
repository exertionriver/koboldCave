package koboldQueue

import com.badlogic.ashley.core.PooledEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.getSystem
import ktx.ashley.with
import org.junit.jupiter.api.Test
import org.river.exertion.ecs.component.action.ActionLookComponent
import org.river.exertion.ecs.component.entity.EntityKoboldComponent
import org.river.exertion.ecs.component.environment.EnvironmentCaveComponent
import org.river.exertion.ecs.system.action.ActionLookSystem
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import org.river.exertion.getEntityComponent
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
//@RunWith(KGdxTestRunner::class)
class TestKoboldActions {

    val engine = PooledEngine().apply { ActionPlexSystem(this) }

    val kobold = EntityKoboldComponent.instantiate(engine, "gragga")

    val koboldSecond = EntityKoboldComponent.instantiate(engine, "krakka")

    val koboldThird = EntityKoboldComponent.instantiate(engine, "razza")

    @Test
    fun testActionEnumeration() {

        kobold.getEntityComponent().baseActions.forEach{ println("base action: $it") }
        kobold.getEntityComponent().extendedActions.forEach{ println("extended action: $it") }

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

        val plexInterval = engine.getSystem<ActionPlexSystem>().initInterval

        (0..1000).forEach {
            runBlocking { delay(plexInterval.toLong() * 1000) }
            println("durstamp:${it * plexInterval}")
            engine.update(plexInterval)
        }

    }

}