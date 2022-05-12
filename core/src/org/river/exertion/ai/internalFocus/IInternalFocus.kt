package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.symbol.PresentSymbolInstance

interface IInternalFocus {

    var tag : String
    var dependsUpon : MutableSet<IInternalFocus>
    var satisfyingStrategies : MutableSet<IInternalFocus>

    fun satisfyingCondition(targetSymbol : PresentSymbolInstance) : Boolean

    fun satisfyingResult(targetSymbol: PresentSymbolInstance) : PresentSymbolInstance
}