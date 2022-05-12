package org.river.exertion.ai.internalFocus.internalFocuses

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.internalFocuses.proximity.CloseIntimateFocus
import org.river.exertion.ai.symbol.PresentSymbolInstance
import org.river.exertion.ai.symbol.ControlSymbolInstance
import org.river.exertion.ai.symbol.SymbolMagnetism
import org.river.exertion.ai.symbol.controlSymbols.SpawnSymbol
import org.river.exertion.ai.symbol.perceivedSymbols.PossessSymbol

object PickUpFocus : IInternalFocus {

    override var tag = "pick up"
    override var dependsUpon = mutableSetOf<IInternalFocus>()
    override var satisfyingStrategies = mutableSetOf<IInternalFocus>()
    override fun satisfyingCondition(targetSymbol : PresentSymbolInstance) = CloseIntimateFocus.satisfyingCondition(targetSymbol)
    override fun satisfyingResult(targetSymbol : PresentSymbolInstance) = targetSymbol.apply {
        var unitsToHandle = 0f

        if (this.units > targetSymbol.handleCapacity) {
            unitsToHandle = targetSymbol.handleCapacity
            this.ornaments.add(ControlSymbolInstance(SpawnSymbol, units = this.units - targetSymbol.handleCapacity, position = this.position).apply { this.target = targetSymbol.symbolObj } )
        } else
            unitsToHandle = this.units

        this.units = unitsToHandle
        this.position = SymbolMagnetism.STABILIZE_POSSESSION.targetPosition()
        this.ornaments.add(ControlSymbolInstance(PossessSymbol))
    }
}