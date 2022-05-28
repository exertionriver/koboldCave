package org.river.exertion.ai.messaging

import org.river.exertion.ai.internalSymbol.core.PresentSymbolInstance

data class PresentSymbolMessage(val presentSymbolInstance: PresentSymbolInstance, val deltaPosition : Float)
