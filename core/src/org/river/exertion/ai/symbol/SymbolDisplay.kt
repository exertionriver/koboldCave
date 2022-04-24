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

    private fun updateSymbolPresent(updateSymbolInstance : SymbolInstance) {

        val (deltaCycle, deltaPosition) = symbolsPresent.first { it.symbolObj == updateSymbolInstance.symbolObj }.normalizeCyclePosition( updateSymbolInstance.position )

        val modifiedSymbolInstances = mutableSetOf<SymbolInstance>()

        //get symbol modifiers, modify accordingly
        symbolsPresent.filter {it.symbolObj != updateSymbolInstance.symbolObj}.filter { symbolInstance -> symbolInstance.symbolObj.modifiers.map { it.modifyingSymbol }.contains (updateSymbolInstance.symbolObj) }.forEach {
            modifiedSymbolInstance -> modifiedSymbolInstance.updateModifiedSymbol(updateSymbolInstance, deltaCycle, deltaPosition)
            modifiedSymbolInstances.add(modifiedSymbolInstance)
        }

        //get symbol spawns for updated symbol, spawn accordingly
        updateSymbolInstance.symbolObj.spawns.forEach {
            spawnSymbolInstance -> spawnSymbolInstance.spawnSymbol(updateSymbolInstance)
        }

        //get symbol for modified symbols, spawn accordingly
        modifiedSymbolInstances.forEach {
            modifiedSymbolInstance -> modifiedSymbolInstance.symbolObj.spawns.forEach { modifiedSymbolSpawn ->
               modifiedSymbolSpawn.spawnSymbol(modifiedSymbolInstance)
            }
        }

        //get symbol despawns, despawn accordingly
        updateSymbolInstance.symbolObj.despawns.forEach {
            despawnSymbolInstance -> despawnSymbolInstance.despawnSymbol(updateSymbolInstance)
        }

        //get symbol for modified symbols, despawn accordingly
        modifiedSymbolInstances.forEach {
            modifiedSymbolInstance -> modifiedSymbolInstance.symbolObj.despawns.forEach { modifiedSymbolDespawn ->
                modifiedSymbolDespawn.despawnSymbol(modifiedSymbolInstance)
            }
        }
    }

    private fun SymbolInstance.updateModifiedSymbol(updateSymbolInstance : SymbolInstance, deltaCycle : Float, deltaPosition : Float) {

        val modifierEntry = this.symbolObj.modifiers.first { it.modifyingSymbol == updateSymbolInstance.symbolObj }

        val modifier = if (modifierEntry.modifyingType == SymbolModifierType.CYCLE_COUNT) {
            if (updateSymbolInstance.symbolObj.targetMagnetism == modifierEntry.modifyingMagnetism) {
                modifierEntry.modifierRatio * deltaCycle
            } else {
                -modifierEntry.modifierRatio * deltaCycle
            }
        } else { //CYCLE_POSITION
            if (updateSymbolInstance.symbolObj.targetMagnetism == modifierEntry.modifyingMagnetism) {
                modifierEntry.modifierRatio * (deltaCycle + deltaPosition)
            } else {
                -modifierEntry.modifierRatio * (deltaCycle + deltaPosition)
            }
        }

        this.normalizeCyclePosition(this.position + modifier)
    }

    private fun SymbolSpawn.spawnSymbol(updateSymbolInstance : SymbolInstance) {

        if (this.thresholdType == SymbolThresholdType.LESS_THAN) {
            if (updateSymbolInstance.position < this.position) symbolsPresent.add(this.spawnSymbol.spawn())
        } else {
            if (updateSymbolInstance.position > this.position) symbolsPresent.add(this.spawnSymbol.spawn())
        }
    }

    private fun SymbolSpawn.despawnSymbol(updateSymbolInstance : SymbolInstance) {

        if (this.thresholdType == SymbolThresholdType.LESS_THAN) {
            if (updateSymbolInstance.position < this.position) symbolsPresent.removeAll(symbolsPresent.filter { it.symbolObj == this.spawnSymbol}.toSet())
        } else {
            if (updateSymbolInstance.position > this.position) symbolsPresent.removeAll(symbolsPresent.filter { it.symbolObj == this.spawnSymbol}.toSet())
        }
    }
}