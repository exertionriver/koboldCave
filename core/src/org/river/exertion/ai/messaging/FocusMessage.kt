package org.river.exertion.ai.messaging

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.InternalFocusInstance
import org.river.exertion.ai.internalSymbol.core.SymbolInstance

data class FocusMessage(var satisfierFocus : IInternalFocus? = null
                        , var absentSymbolInstance : SymbolInstance? = null
                        , var presentSymbolInstance : SymbolInstance? = null
                        , var chainStrategyInstance : InternalFocusInstance? = null)
