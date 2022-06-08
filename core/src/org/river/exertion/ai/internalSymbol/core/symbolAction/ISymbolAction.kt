package org.river.exertion.ai.internalSymbol.core.symbolAction

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.SymbolActionType
import org.river.exertion.ai.internalSymbol.core.SymbolDisplayType
import org.river.exertion.ai.internalSymbol.core.SymbolInstance

//templates included with IPerceivedSymbols
interface ISymbolAction {
    var symbolActionType : SymbolActionType
    fun execute(entity : Telegraph, sourceSymbolInstance : SymbolInstance, sourceDisplayType: SymbolDisplayType)
}