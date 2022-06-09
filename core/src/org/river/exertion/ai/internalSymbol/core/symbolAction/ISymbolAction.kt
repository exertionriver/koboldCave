package org.river.exertion.ai.internalSymbol.core.symbolAction

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.IPerceivedSymbol
import org.river.exertion.ai.internalSymbol.core.SymbolActionType
import org.river.exertion.ai.internalSymbol.core.SymbolDisplayType
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.messaging.SymbolMessage

//templates included with IPerceivedSymbols
interface ISymbolAction {
    var symbolActionType : SymbolActionType
    fun execute(entity : Telegraph, symbolMessage : SymbolMessage)
}