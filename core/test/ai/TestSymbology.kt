package ai

import org.junit.jupiter.api.Test
import org.river.exertion.ai.symbol.*
import org.river.exertion.ai.symbol.perceivedSymbols.FoodSymbol
import org.river.exertion.ai.symbol.perceivedSymbols.HungerSymbol
import org.river.exertion.ai.symbol.perceivedSymbols.MomentElapseSymbol


@ExperimentalUnsignedTypes
class TestSymbology {

    @Test
    fun testDisplayUpdate() {

        val symbolDisplay = SymbolDisplay().apply {
            this.symbolsPresent = mutableSetOf(
                PresentSymbolInstance(HungerSymbol, .55f),
                PresentSymbolInstance(FoodSymbol, .6f),
                PresentSymbolInstance(MomentElapseSymbol, .4f)
            )
            this.symbolsAbsent.add(AbsentSymbolInstance(FoodSymbol, 4.5f, 0.45f))

        }

        println("initial values")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }

        val updateSymbols1 = mutableSetOf(
                PresentSymbolInstance(HungerSymbol, .5f),
        )

        symbolDisplay.update(updateSymbols1)

        println("after first update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }

        val updateSymbols2 = mutableSetOf(
                PresentSymbolInstance(MomentElapseSymbol, -5000.2f)
        )

        symbolDisplay.update(updateSymbols2)

        println("after second update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }

        val updateSymbols3 = mutableSetOf(
                PresentSymbolInstance(MomentElapseSymbol, -900.2f)
        )

        symbolDisplay.update(updateSymbols3)

        println("after third update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }

        val updateSymbols4 = mutableSetOf(
                PresentSymbolInstance(FoodSymbol,-2.4f)
        )

        symbolDisplay.update(updateSymbols4)

        println("after four update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}, ${it.impact}") }

    }
}