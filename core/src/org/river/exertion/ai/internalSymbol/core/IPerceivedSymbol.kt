package org.river.exertion.ai.internalSymbol.core

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.symbolAction.ISymbolAction

interface IPerceivedSymbol : ISymbol {

    override var tag : String
    override var type : SymbolType
    var targetPosition : SymbolTargetPosition
    var cycle : SymbolCycle

    var symbolActions : MutableSet<ISymbolAction>
    var focusSatisfiers : MutableSet<IInternalFocus>

    fun spawn() : SymbolInstance

    @Suppress("NewApi")
    fun despawnAll(targetDisplay : MutableSet<SymbolInstance>) : MutableSet<SymbolInstance> {
        targetDisplay.removeIf { it.symbolObj.tag == this.tag }
        return targetDisplay
    }
}