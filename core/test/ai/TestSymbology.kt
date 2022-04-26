package ai

import org.junit.jupiter.api.Test
import org.river.exertion.ai.symbol.*
import org.river.exertion.ai.symbol.symbols.FoodSymbol
import org.river.exertion.ai.symbol.symbols.HungerSymbol
import org.river.exertion.ai.symbol.symbols.TimeElapseSymbol


@ExperimentalUnsignedTypes
class TestSymbology {

    @Test
    fun testDisplayUpdate() {

        val symbolDisplay = SymbolDisplay().apply {
            this.symbolsPresent = mutableSetOf(
                SymbolInstance(HungerSymbol, .8f),
                SymbolInstance(FoodSymbol, .6f),
                SymbolInstance(TimeElapseSymbol, .4f)
            )
            this.symbolsAbsent.add(SymbolInstance(FoodSymbol, 2.0f))

        }

        println("initial values")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}") }

        val updateSymbols1 = mutableSetOf(
                SymbolInstance(HungerSymbol, .7f),
        )

        symbolDisplay.update(updateSymbols1)

        println("after first update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}") }

        val updateSymbols2 = mutableSetOf(
                SymbolInstance(TimeElapseSymbol, -5000.2f)
        )

        symbolDisplay.update(updateSymbols2)

        println("after second update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}") }

        val updateSymbols3 = mutableSetOf(
                SymbolInstance(FoodSymbol,-2.4f)
        )

        symbolDisplay.update(updateSymbols3)

        println("after third update")
        println("present:")
        symbolDisplay.symbolsPresent.forEach { println("${it.symbolObj} : ${it.position}") }
        println("absent:")
        symbolDisplay.symbolsAbsent.forEach { println("${it.symbolObj} : ${it.position}") }

    }
}