package org.river.exertion.ai.messaging

import org.river.exertion.ai.internalSymbol.core.IPerceivedSymbol
import org.river.exertion.ai.internalSymbol.core.SymbolDisplayType
import org.river.exertion.ai.internalSymbol.core.SymbolInstance

data class SymbolMessage(var symbol : IPerceivedSymbol? = null
                        , var symbolInstance : SymbolInstance? = null
                        , var symbolDisplayType: SymbolDisplayType? = null
                        , var targetSymbolInstance : SymbolInstance? = null
                        , var targetSymbolDisplayType : SymbolDisplayType? = null) {


}