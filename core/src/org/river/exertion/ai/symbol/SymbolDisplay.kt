package org.river.exertion.ai.symbol

class SymbolDisplay {

    var symbolsPresent = mutableSetOf<PresentSymbolInstance>()
    var symbolsAbsent = mutableSetOf<AbsentSymbolInstance>()

    fun addUpdate(addUpdateSymbols : MutableSet<PresentSymbolInstance>) {

        addUpdateSymbols.forEach { symbolInstance ->
            if ( symbolsPresent.map { it.symbolObj }.contains(symbolInstance.symbolObj) ) {
                updateSymbolPresent(symbolInstance)
            } else symbolsPresent.add(symbolInstance)
        }
    }

    fun update(updateSymbols : MutableSet<PresentSymbolInstance>) {

        updateSymbols.forEach { presentSymbolInstance ->
            if ( symbolsPresent.map { it.symbolObj }.contains(presentSymbolInstance.symbolObj) ) {
                updateSymbolPresent(presentSymbolInstance)
            }
        }
        symbolsAbsent.forEach { absentSymbolInstance ->
            absentSymbolInstance.updateSymbolAbsent()
        }
    }

    private fun PresentSymbolInstance.normalizeCyclePosition(updatePosition : Float) : Pair<Float, Float> {

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

    private fun updateSymbolPresent(updateSymbolInstance : PresentSymbolInstance) {

        val (deltaCycle, deltaPosition) = symbolsPresent.first { it.symbolObj == updateSymbolInstance.symbolObj }.normalizeCyclePosition( updateSymbolInstance.position )

        val modifiedSymbolInstances = mutableSetOf<PresentSymbolInstance>()

        //get symbol modifiers, modify accordingly
        symbolsPresent.filter {it.symbolObj != updateSymbolInstance.symbolObj}.filter { symbolInstance -> symbolInstance.symbolObj.presentModifiers.map { it.modifyingSymbol }.contains (updateSymbolInstance.symbolObj) }.forEach {
            modifiedSymbolInstance -> modifiedSymbolInstance.updateModifiedSymbol(updateSymbolInstance, deltaCycle, deltaPosition)
            modifiedSymbolInstances.add(modifiedSymbolInstance)
        }

        //get symbol spawns for updated symbol, spawn accordingly
        updateSymbolInstance.symbolObj.spawnsPresent.forEach {
            spawnSymbolInstance -> spawnSymbolInstance.spawnSymbol(updateSymbolInstance)
        }

        //get symbol for modified symbols, spawn accordingly
        modifiedSymbolInstances.forEach {
                modifiedSymbolInstance -> modifiedSymbolInstance.symbolObj.spawnsPresent.forEach { modifiedSymbolSpawn ->
                modifiedSymbolSpawn.spawnSymbol(modifiedSymbolInstance)
            }
        }

        //get symbol despawns, despawn accordingly
        updateSymbolInstance.symbolObj.despawnsPresent.forEach {
            despawnSymbolInstance -> despawnSymbolInstance.despawnSymbol(updateSymbolInstance)
        }

        //get symbol for modified symbols, despawn accordingly
        modifiedSymbolInstances.forEach {
                modifiedSymbolInstance -> modifiedSymbolInstance.symbolObj.despawnsPresent.forEach { modifiedSymbolDespawn ->
                modifiedSymbolDespawn.despawnSymbol(modifiedSymbolInstance)
            }
        }

    }

    private fun AbsentSymbolInstance.updateSymbolAbsent() {

        var position = 0f
        var impact = 0f

        //if these modified and update symbols are magnetism-aligned, spawn 'absent' symbolinstance if not already spawned
        this.symbolObj.absentImpactors.forEach { absentImpactor ->
            val modifierRatio = absentImpactor.modifierRatio
            val impactorRatio = absentImpactor.impactorRatio

            val presentSymbolInstance = symbolsPresent.firstOrNull { it.symbolObj == absentImpactor.modifyingSymbol }

            if (presentSymbolInstance != null) {
                position += if (modifierRatio == 0f) 0f else
                        if (this.symbolObj.targetMagnetism == SymbolMagnetism.ATTRACT)
                            (presentSymbolInstance.symbolObj.targetMagnetism.targetPosition() - presentSymbolInstance.position) / modifierRatio
                        else
                            -(presentSymbolInstance.symbolObj.targetMagnetism.targetPosition() - presentSymbolInstance.position) / modifierRatio
                impact += if (impactorRatio == 0f) 0f else
                        if (this.symbolObj.targetMagnetism == SymbolMagnetism.ATTRACT)
                            (presentSymbolInstance.symbolObj.targetMagnetism.targetPosition() - presentSymbolInstance.position) / impactorRatio
                        else
                            -(presentSymbolInstance.symbolObj.targetMagnetism.targetPosition() - presentSymbolInstance.position) / impactorRatio

            }
        }

        this.position = position
        this.impact = impact
    }

        //e.g. SymbolInstance == Hunger, updateSymbolInstance == Food
    private fun PresentSymbolInstance.updateModifiedSymbol(updateSymbolInstance : PresentSymbolInstance, deltaCycle : Float, deltaPosition : Float) {

        val modifierEntry = this.symbolObj.presentModifiers.first { it.modifyingSymbol == updateSymbolInstance.symbolObj }

        //modify e.g. hunger with food count
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

    private fun SymbolSpawn.spawnSymbol(updateSymbolInstance : PresentSymbolInstance) {

        if (this.thresholdType == SymbolThresholdType.LESS_THAN) {
            if (updateSymbolInstance.position < this.position && !symbolsPresent.map { it.symbolObj }.contains(this.spawnSymbol)) symbolsPresent.addAll(this.spawnSymbol.spawnPresent())
        } else {
            if (updateSymbolInstance.position > this.position && !symbolsPresent.map { it.symbolObj }.contains(this.spawnSymbol)) symbolsPresent.addAll(this.spawnSymbol.spawnPresent())
        }
    }

    @Suppress("NewApi")
    private fun SymbolSpawn.despawnSymbol(updateSymbolInstance : PresentSymbolInstance) {

        if (this.thresholdType == SymbolThresholdType.LESS_THAN) {
            if (updateSymbolInstance.position < this.position) symbolsPresent.removeIf { it.symbolObj == this.spawnSymbol}
        } else {
            if (updateSymbolInstance.position > this.position) symbolsPresent.removeIf { it.symbolObj == this.spawnSymbol}
        }
    }
}