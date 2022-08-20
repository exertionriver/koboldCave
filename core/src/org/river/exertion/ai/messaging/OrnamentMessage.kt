package org.river.exertion.ai.messaging

import org.river.exertion.ai.internalSymbol.core.ISymbol
import org.river.exertion.ai.internalSymbol.core.SymbolInstance

data class OrnamentMessage(var symbolInstance : SymbolInstance? = null,
                           var ornament : ISymbol? = null)