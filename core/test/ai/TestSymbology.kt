package ai

import com.badlogic.ashley.core.PooledEngine
import org.junit.jupiter.api.Test
import org.river.exertion.ai.internalSymbol.core.InternalSymbolDisplay
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolModifyAction
import org.river.exertion.ai.internalSymbol.perceivedSymbols.FoodSymbol
import org.river.exertion.ai.internalSymbol.perceivedSymbols.HungerSymbol
import org.river.exertion.ai.internalSymbol.perceivedSymbols.MomentElapseSymbol
import org.river.exertion.ai.messaging.SymbolMessage
import org.river.exertion.ecs.component.SymbologyComponent
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.component.action.ActionSimpleDecideMoveComponent
import org.river.exertion.ecs.entity.IEntity
import org.river.exertion.ecs.entity.character.CharacterKobold
import org.river.exertion.ecs.system.SystemManager


@ExperimentalUnsignedTypes
class TestSymbology {

    val engine = PooledEngine().apply { SystemManager.init(this) }
    val character = CharacterKobold.ecsInstantiate(engine).apply { this.remove(ActionMoveComponent.getFor(this)!!.javaClass) ; this.remove(ActionSimpleDecideMoveComponent.getFor(this)!!.javaClass) }
    val secondCharacter = CharacterKobold.ecsInstantiate(engine).apply { this.remove(ActionMoveComponent.getFor(this)!!.javaClass) ; this.remove(ActionSimpleDecideMoveComponent.getFor(this)!!.javaClass) }

    @Test
    fun testDisplayUpdate() {

        SymbologyComponent.getFor(character)!!.internalSymbology.internalSymbolDisplay.symbolDisplay = mutableSetOf(
                    SymbolInstance(HungerSymbol, cycles = 1f, position = .55f),
                    SymbolInstance(FoodSymbol, cycles = 12f, position = .6f).apply { this.consumeCapacity = 1f; this.handleCapacity = 3f},
                    SymbolInstance(MomentElapseSymbol, cycles = -1f, position = .4f)
            )

        (0..30).forEach {
            val internalFocusDisplay = SymbologyComponent.getFor(character)!!.internalSymbology.internalFocusDisplay
            val internalSymbolDisplay = SymbologyComponent.getFor(character)!!.internalSymbology.internalSymbolDisplay

            println("itr:$it")
            internalSymbolDisplay.symbolDisplay.forEach { println("${it.symbolObj} : ${it.cycles}, ${it.position}, ${it.displayType}") }
            println("internal focuses:")
            internalFocusDisplay.focusPlansPresent.forEachIndexed { idx, it -> println (it.absentSymbolInstance.symbolObj) ; it.instancesChain.forEach { println("$idx; $it") } }

            engine.update(.1f)

            if (it == 20) {
                SymbolModifyAction.executeImmediate(IEntity.getFor(character)!!, SymbolMessage(symbolInstance = SymbologyComponent.getFor(character)!!.internalSymbology.internalSymbolDisplay.symbolDisplay.first { it.symbolObj == HungerSymbol }.apply { this.deltaPosition = -.3f}) )
            }
        }
    }
}