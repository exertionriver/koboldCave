package org.river.exertion.ai.messaging

import org.river.exertion.ai.internalSymbol.core.SymbolDisplayType
import org.river.exertion.ai.internalSymbol.core.SymbolInstance

data class SymbolMessage(val symbolInstance : SymbolInstance, val symbolDisplayType : SymbolDisplayType)
