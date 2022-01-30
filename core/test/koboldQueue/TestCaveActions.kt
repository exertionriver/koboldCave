package koboldQueue

import com.badlogic.ashley.core.PooledEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import ktx.ashley.getSystem
import org.junit.jupiter.api.Test
import org.river.exertion.ecs.component.environment.EnvironmentCave
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import org.river.exertion.getEnvironmentComponent
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
//@RunWith(KGdxTestRunner::class)
class TestCaveActions {

    val engine = PooledEngine().apply { ActionPlexSystem(this) }

    val cave = EnvironmentCave.instantiate(engine, "spookyCave")

    @Test
    fun testActionEnumeration() {

        cave.getEnvironmentComponent().baseActions.forEach{ println("base action: $it") }
        cave.getEnvironmentComponent().extendedActions.forEach{ println("extended action: $it") }

        cave.components.forEach { println("$it") }
    }

    @Test
    fun testActionPlex() {

        val plexInterval = engine.getSystem<ActionPlexSystem>().initInterval

        (0..10000).forEach {
            runBlocking { delay(plexInterval.toLong() * 1000) }
//            println("durstamp:${it * plexInterval}")
            engine.update(plexInterval)
        }

    }

}