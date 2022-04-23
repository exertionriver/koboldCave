package org.river.exertion.ai.symbol

class SymbolDisplay {

    var symbolsPresent = mutableSetOf<SymbolInstance>()

    fun addUpdate(addUpdateSymbols : MutableSet<SymbolInstance>) {

        addUpdateSymbols.forEach { symbolInstance ->
            if ( symbolsPresent.map { it.symbolObj }.contains(symbolInstance.symbolObj) ) {
                updateSymbolPresent(symbolInstance)
            } else symbolsPresent.add(symbolInstance)
        }
    }

    fun update(updateSymbols : MutableSet<SymbolInstance>) {

        updateSymbols.forEach { symbolInstance ->
            if ( symbolsPresent.map { it.symbolObj }.contains(symbolInstance.symbolObj) ) {
                updateSymbolPresent(symbolInstance)
            }
        }
    }

    private fun SymbolInstance.normalizeCyclePosition(updatePosition : Float) : Pair<Float, Float> {

        var deltaCycle = 0f
        val deltaPosition = updatePosition - this.position

        this.position = updatePosition

        //first update position wrt symbol cycle style
        if (this.symbolObj.cycle == SymbolCycle.MULTIPLE) {
            while (this.position < 0) {
                deltaCycle -= 1
                this.position += 1
            }
            while (this.position > 1) {
                deltaCycle += 1
                this.position -= 1
            }
        } else { //single or none
            if (this.position < 0) this.position = 0f
            if (this.position > 1) this.position = 1f
        }

        return Pair(deltaCycle, deltaPosition - deltaCycle)
    }

    private fun updateSymbolPresent(updateSymbol : SymbolInstance) {

        val (deltaCycle, deltaPosition) = symbolsPresent.first { it.symbolObj == updateSymbol.symbolObj }.normalizeCyclePosition( updateSymbol.position )

        //next, get symbol modifiers, modify accordingly
        symbolsPresent.filter {it.symbolObj != updateSymbol.symbolObj}.filter { prevSymbolInstance -> prevSymbolInstance.symbolObj.modifiers.map { it.modifyingSymbol }.contains (updateSymbol.symbolObj) }.forEach { modifiedSymbolInstance ->
            val modifierEntry = modifiedSymbolInstance.symbolObj.modifiers.first { it.modifyingSymbol == updateSymbol.symbolObj }

            val modifier = if (modifierEntry.modifyingType == SymbolModifierType.CYCLE_COUNT) {
                if (updateSymbol.symbolObj.targetMagnetism == modifierEntry.modifyingMagnetism) {
                    modifierEntry.modifierRatio * deltaCycle
                } else {
                    -modifierEntry.modifierRatio * deltaCycle
                }
            } else { //CYCLE_POSITION
                if (updateSymbol.symbolObj.targetMagnetism == modifierEntry.modifyingMagnetism) {
                    modifierEntry.modifierRatio * (deltaCycle + deltaPosition)
                } else {
                    -modifierEntry.modifierRatio * (deltaCycle + deltaPosition)
                }
            }

            modifiedSymbolInstance.normalizeCyclePosition(modifiedSymbolInstance.position + modifier)
        }
    }
}