package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.symbol.SymbolDisplay

class InternalFocusDisplay {

    var focusPlansPresent = mutableSetOf<InternalFocusPlan>()

    fun update(symbolDisplay: SymbolDisplay) : SymbolDisplay {

        //add new plan
        symbolDisplay.symbolsAbsent.filter { it.symbolObj.satisfiers.isNotEmpty() }.forEach { absentSymbolInstance ->
            val satisfier = absentSymbolInstance.symbolObj.satisfiers.firstOrNull()

            //add absentSymbolInstance satisfier chain to focusesPresent if not already added
            if ( (satisfier != null) && (!focusPlansPresent.map { it.absentSymbolInstance }.contains(absentSymbolInstance) ) )
                focusPlansPresent.add(InternalFocusPlan(absentSymbolInstance).apply { this.init(symbolDisplay.symbolsPresent) } )
        }

        //execute plans
        focusPlansPresent.sortedByDescending { it.absentSymbolInstance.impact }.forEach { focusPlan ->
            val satisfySymbol = symbolDisplay.symbolsPresent.sortedBy { it.position }.firstOrNull { it.symbolObj == focusPlan.absentSymbolInstance.symbolObj }

            if ( (satisfySymbol != null) && focusPlan.instancesChain.isNotEmpty() ) {
                focusPlan.instancesChain.asReversed().forEach { internalFocusInstance ->
                    if (internalFocusInstance.internalFocusObj.satisfyingCondition(satisfySymbol) ) {
                        //update symbol
//                        symbolDisplay.symbolsPresent.remove(satisfySymbol)
                        symbolDisplay.update(mutableSetOf(internalFocusInstance.internalFocusObj.satisfyingResult(satisfySymbol) ) )
                    }
                }
                if (!symbolDisplay.symbolsAbsent.map { it.symbolObj }.contains(focusPlan.absentSymbolInstance.symbolObj) )
                    focusPlansPresent.remove(focusPlan)
                else
                    focusPlan.update(symbolDisplay.symbolsPresent)
            }
        }

//        prevSymbolDisplay.update(updateDisplay.symbolsPresent)

        return symbolDisplay
    }
}