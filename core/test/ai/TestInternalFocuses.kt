package ai

import org.junit.jupiter.api.Test
import org.river.exertion.ai.internalFocus.InternalFocusDisplay
import org.river.exertion.ai.symbol.*
import org.river.exertion.ai.symbol.perceivedSymbols.FoodSymbol
import org.river.exertion.ai.symbol.perceivedSymbols.HungerSymbol
import org.river.exertion.ai.symbol.perceivedSymbols.MomentElapseSymbol


@ExperimentalUnsignedTypes
class TestInternalFocuses {

    @Test
    fun testDisplayUpdate() {

        var symbolDisplay = SymbolDisplay().apply {
            this.symbolsPresent = mutableSetOf(
                PresentSymbolInstance(HungerSymbol, position = .55f),
                PresentSymbolInstance(FoodSymbol, units = 12f, position = .6f).apply { this.consumeCapacity = 1f; this.handleCapacity = 3f},
                PresentSymbolInstance(MomentElapseSymbol, position = .4f)
            )
            this.symbolsAbsent.add(AbsentSymbolInstance(FoodSymbol, 4.5f, 0.45f))

        }

        val internalFocusDisplay = InternalFocusDisplay()

        println("initial values")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.units}, ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }
        println("internal focuses:")
        internalFocusDisplay.focusPlansPresent.forEach { println (it.absentSymbolInstance.symbolObj) ; it.instancesChain.forEach { println(it) } }

        symbolDisplay = internalFocusDisplay.update(symbolDisplay)

        println("first update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.units}, ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }
        println("internal focuses:")
        internalFocusDisplay.focusPlansPresent.forEach { println (it.absentSymbolInstance.symbolObj) ; it.instancesChain.forEach { println(it) } }

        symbolDisplay = internalFocusDisplay.update(symbolDisplay)

        println("second update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.units}, ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }
        println("internal focuses:")
        internalFocusDisplay.focusPlansPresent.forEach { println (it.absentSymbolInstance.symbolObj) ; it.instancesChain.forEach { println(it) } }

        (0 until 6).forEach {
            symbolDisplay = internalFocusDisplay.update(symbolDisplay)
            symbolDisplay.update(symbolDisplay.symbolsPresent)
        }

        println("eighth update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.units}, ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }
        println("internal focuses:")
        internalFocusDisplay.focusPlansPresent.forEach { println (it.absentSymbolInstance.symbolObj) ; it.instancesChain.forEach { println(it) }; println(it.satisied) }

        symbolDisplay = internalFocusDisplay.update(symbolDisplay)

        println("ninth update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.units}, ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }
        println("internal focuses:")
        internalFocusDisplay.focusPlansPresent.forEach { println (it.absentSymbolInstance.symbolObj) ; it.instancesChain.forEach { println(it) }; println(it.satisied) }

        symbolDisplay = internalFocusDisplay.update(symbolDisplay)

        println("tenth update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.units}, ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }
        println("internal focuses:")
        internalFocusDisplay.focusPlansPresent.forEach { println (it.absentSymbolInstance.symbolObj) ; it.instancesChain.forEach { println(it) }; println(it.satisied) }

        symbolDisplay = internalFocusDisplay.update(symbolDisplay)

        println("eleventh update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.units}, ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }
        println("internal focuses:")
        internalFocusDisplay.focusPlansPresent.forEach { println (it.absentSymbolInstance.symbolObj) ; it.instancesChain.forEach { println(it) }; println(it.satisied) }

        symbolDisplay = internalFocusDisplay.update(symbolDisplay)

        println("twelfth update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.units}, ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }
        println("internal focuses:")
        internalFocusDisplay.focusPlansPresent.forEach { println (it.absentSymbolInstance.symbolObj) ; it.instancesChain.forEach { println(it) }; println(it.satisied) }

        symbolDisplay.update(mutableSetOf(symbolDisplay.symbolsPresent.first { it.symbolObj == HungerSymbol }.apply { position = .45f }))
        symbolDisplay = internalFocusDisplay.update(symbolDisplay)

        println("thirteenth update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.units}, ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }
        println("internal focuses:")
        internalFocusDisplay.focusPlansPresent.forEach { println (it.absentSymbolInstance.symbolObj) ; it.instancesChain.forEach { println(it) }; println(it.satisied) }
    }
}