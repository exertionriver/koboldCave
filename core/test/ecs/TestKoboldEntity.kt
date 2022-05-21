package ecs

import com.badlogic.ashley.core.PooledEngine
import org.junit.jupiter.api.Test
import org.river.exertion.ecs.entity.character.CharacterKobold
import org.river.exertion.ecs.system.SystemManager

@ExperimentalUnsignedTypes
class TestKoboldEntity {

    val engine = PooledEngine().apply { SystemManager.init(this) }

    @Test
    fun testKoboldEntity() {
        val koboldCharacter = CharacterKobold.ecsInstantiate(engine)
    }
}