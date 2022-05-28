package ai

import com.badlogic.ashley.core.PooledEngine
import org.junit.jupiter.api.Test
import org.river.exertion.ai.internalFocus.InternalFocusDisplay
import org.river.exertion.ai.internalSymbol.core.AbsentSymbolInstance
import org.river.exertion.ai.internalSymbol.core.InternalSymbolDisplay
import org.river.exertion.ai.internalSymbol.core.PresentSymbolInstance
import org.river.exertion.ai.internalSymbol.perceivedSymbols.FoodSymbol
import org.river.exertion.ai.internalSymbol.perceivedSymbols.HungerSymbol
import org.river.exertion.ai.internalSymbol.perceivedSymbols.MomentElapseSymbol
import org.river.exertion.ecs.component.SymbologyComponent
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.component.action.ActionSimpleDecideMoveComponent
import org.river.exertion.ecs.entity.character.CharacterKobold
import org.river.exertion.ecs.system.SystemManager


@ExperimentalUnsignedTypes
class TestSymbology {

    val engine = PooledEngine().apply { SystemManager.init(this) }
    val character = CharacterKobold.ecsInstantiate(engine).apply { this.remove(ActionMoveComponent.getFor(this)!!.javaClass) ; this.remove(ActionSimpleDecideMoveComponent.getFor(this)!!.javaClass) }
    val secondCharacter = CharacterKobold.ecsInstantiate(engine).apply { this.remove(ActionMoveComponent.getFor(this)!!.javaClass) ; this.remove(ActionSimpleDecideMoveComponent.getFor(this)!!.javaClass) }

    @Test
    fun testDisplayUpdate() {

        var symbolDisplay = InternalSymbolDisplay().apply {
            this.symbolsPresent = mutableSetOf(
                PresentSymbolInstance(HungerSymbol, position = .55f),
                PresentSymbolInstance(FoodSymbol, cycles = 12f, position = .6f).apply { this.consumeCapacity = 1f; this.handleCapacity = 3f},
                PresentSymbolInstance(MomentElapseSymbol, position = .4f)
            )
            this.symbolsAbsent.add(AbsentSymbolInstance(FoodSymbol, 4.5f, 0.45f))

        }

        SymbologyComponent.getFor(character)!!.internalSymbology.internalSymbolDisplay = symbolDisplay

        val internalFocusDisplay = InternalFocusDisplay()

        println("initial values")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.cycles}, ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }
        println("internal focuses:")
        internalFocusDisplay.focusPlansPresent.forEach { println (it.absentSymbolInstance.symbolObj) ; it.instancesChain.forEach { println(it) } }

        engine.update(.1f )

        println("first update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.cycles}, ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }
        println("internal focuses:")
        internalFocusDisplay.focusPlansPresent.forEach { println (it.absentSymbolInstance.symbolObj) ; it.instancesChain.forEach { println(it) } }

        engine.update(.1f )

        println("second update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.cycles}, ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }
        println("internal focuses:")
        internalFocusDisplay.focusPlansPresent.forEach { println (it.absentSymbolInstance.symbolObj) ; it.instancesChain.forEach { println(it) } }

        (0 until 6).forEach {
            engine.update(.1f)
        }

        println("eighth update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.cycles}, ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }
        println("internal focuses:")
        internalFocusDisplay.focusPlansPresent.forEach { println (it.absentSymbolInstance.symbolObj) ; it.instancesChain.forEach { println(it) }; println(it.satisied) }

        engine.update(.1f)

        println("ninth update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.cycles}, ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }
        println("internal focuses:")
        internalFocusDisplay.focusPlansPresent.forEach { println (it.absentSymbolInstance.symbolObj) ; it.instancesChain.forEach { println(it) }; println(it.satisied) }

        engine.update(.1f)

        println("tenth update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.cycles}, ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }
        println("internal focuses:")
        internalFocusDisplay.focusPlansPresent.forEach { println (it.absentSymbolInstance.symbolObj) ; it.instancesChain.forEach { println(it) }; println(it.satisied) }

        engine.update(.1f)

        println("eleventh update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.cycles}, ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }
        println("internal focuses:")
        internalFocusDisplay.focusPlansPresent.forEach { println (it.absentSymbolInstance.symbolObj) ; it.instancesChain.forEach { println(it) }; println(it.satisied) }

        engine.update(.1f)

        println("twelfth update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.cycles}, ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }
        println("internal focuses:")
        internalFocusDisplay.focusPlansPresent.forEach { println (it.absentSymbolInstance.symbolObj) ; it.instancesChain.forEach { println(it) }; println(it.satisied) }

     //   symbolDisplay.update(mutableSetOf(symbolDisplay.symbolsPresent.first { it.symbolObj == HungerSymbol }.apply { position = .45f }))
        engine.update(.1f)

        println("thirteenth update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.cycles}, ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }
        println("internal focuses:")
        internalFocusDisplay.focusPlansPresent.forEach { println (it.absentSymbolInstance.symbolObj) ; it.instancesChain.forEach { println(it) }; println(it.satisied) }
    }
}