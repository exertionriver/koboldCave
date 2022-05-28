package org.river.exertion.ai.messaging

import org.river.exertion.ai.internalSymbol.core.AbsentSymbolInstance

data class AbsentSymbolMessage(val absentSymbolInstance: AbsentSymbolInstance, val deltaPosition : Float)
