package org.river.exertion.ai.internalSymbol.core

data class SymbolAction(var symbolActionType : SymbolActionType) {

    var sourceSymbol = SymbolInstance()
    var sourceSymbolDisplay = SymbolDisplayType.PRESENT
    var targetSymbol = SymbolInstance()
    var targetSymbolDisplay = SymbolDisplayType.PRESENT

    var modifyCycle = 0f
    var modifyPosition = 0f
    var modifyImpact = 0f

    var symbolModifierType = SymbolModifierType.NONE //source to target
}