package ai

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.river.exertion.ai.internalSymbol.perceivedSymbols.FriendSymbol
import org.river.exertion.ai.internalSymbol.perceivedSymbols.UnknownSymbol
import org.river.exertion.ai.messaging.FacetMessage
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage
import org.river.exertion.ecs.component.SymbologyComponent
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.component.action.ActionSimpleDecideMoveComponent
import org.river.exertion.ecs.entity.IEntity
import org.river.exertion.ecs.entity.character.CharacterKobold
import org.river.exertion.ecs.system.SystemManager


@ExperimentalUnsignedTypes
class TestMessaging {

    val engine = PooledEngine().apply { SystemManager.init(this) }
    val character = CharacterKobold.ecsInstantiate(engine).apply { this.remove(ActionMoveComponent.getFor(this)!!.javaClass) ; this.remove(ActionSimpleDecideMoveComponent.getFor(this)!!.javaClass) }
    val secondCharacter = CharacterKobold.ecsInstantiate(engine).apply { this.remove(ActionMoveComponent.getFor(this)!!.javaClass) ; this.remove(ActionSimpleDecideMoveComponent.getFor(this)!!.javaClass) }

    class ReceiveTest : Telegraph {
        init { MessageChannel.INT_SYMBOL_SPAWN.enableReceive(this)}

        fun falseSpawn(facetMessage: FacetMessage) {}


        override fun handleMessage(msg: Telegram?): Boolean {

            try {
                falseSpawn(MessageChannel.INT_SYMBOL_SPAWN.receiveMessage(msg!!.extraInfo))
            } catch (e : Exception) {
                println("caught exception:$e")
            }

            try {
                MessageChannel.INT_SYMBOL_SPAWN.receiveMessage<TestMessaging>(msg!!.extraInfo)
            } catch (e : Exception) {
                println("caught exception:$e")
            }

            return true
        }
    }

    @Test
    fun testMessageChannel() {

        println ("${MessageChannel.INT_SYMBOL_SPAWN} id : ${MessageChannel.INT_SYMBOL_SPAWN.id()}")

        MessageChannel.INT_SYMBOL_SPAWN.send(IEntity.getFor(character)!!, SymbolMessage(symbol = FriendSymbol))

        assertTrue(SymbologyComponent.getFor(character)!!.internalSymbology.internalSymbolDisplay.symbolDisplay.any { it.symbolObj == FriendSymbol })

        //should break
        try {
            MessageChannel.INT_SYMBOL_SPAWN.send(IEntity.getFor(character)!!, FacetMessage())
        } catch (e : Exception) {
            println("caught exception:$e")
        }

        //testing bad receipt
        ReceiveTest()

        MessageChannel.INT_SYMBOL_SPAWN.send(IEntity.getFor(secondCharacter)!!, SymbolMessage(symbol = UnknownSymbol))

        assertTrue(SymbologyComponent.getFor(secondCharacter)!!.internalSymbology.internalSymbolDisplay.symbolDisplay.any { it.symbolObj == UnknownSymbol })

    }

}