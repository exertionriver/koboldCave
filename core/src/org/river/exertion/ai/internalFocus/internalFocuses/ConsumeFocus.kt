package org.river.exertion.ai.internalFocus.internalFocuses

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.symbol.PresentSymbolInstance
import org.river.exertion.ai.symbol.ControlSymbolInstance
import org.river.exertion.ai.symbol.controlSymbols.DespawnSymbol
import org.river.exertion.ai.symbol.controlSymbols.SpawnSymbol

object ConsumeFocus : IInternalFocus {

    override var tag = "consume"
    override var dependsUpon = mutableSetOf<IInternalFocus>(
        HandleFocus
    )
    override var satisfyingStrategies = mutableSetOf<IInternalFocus>(
        HandleFocus
    )
    override fun satisfyingCondition(targetSymbol : PresentSymbolInstance) = HandleFocus.satisfyingCondition(targetSymbol)

    override fun satisfyingResult(targetSymbol : PresentSymbolInstance) = targetSymbol.apply {
        var unitsToConsume = 0f

        if (this.units > targetSymbol.consumeCapacity) {
            unitsToConsume = targetSymbol.consumeCapacity
            this.ornaments.add(ControlSymbolInstance(SpawnSymbol, units = this.units - targetSymbol.consumeCapacity, position = this.position).apply { this.target = targetSymbol.symbolObj } )
        } else
            unitsToConsume = this.units

        this.units = unitsToConsume
        this.position = -unitsToConsume
        this.ornaments.add(ControlSymbolInstance(DespawnSymbol))
    }
}