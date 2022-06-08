package org.river.exertion.ai.internalFocus

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.SymbolInstance

interface IInternalFocus {

    var tag : String
    var dependsUpon : MutableSet<IInternalFocus>
    var satisfyingStrategies : MutableSet<IInternalFocus>

    fun satisfyingCondition(targetSymbol : SymbolInstance) : Boolean

    fun satisfyingResult(entity: Telegraph, targetSymbol: SymbolInstance)
}