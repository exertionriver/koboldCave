package org.river.exertion.ai.internalSymbol.core;

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.InternalFocusDisplay

class SymbologyInstance(val initSymbology : IInternalSymbology? = null) : IInternalSymbology {

    override var internalSymbolLexicon = mutableSetOf<IInternalSymbol>()
    override var internalFocusesLexicon = mutableSetOf<IInternalFocus>()

    override var internalSymbolDisplay = InternalSymbolDisplay()
    override var internalFocusDisplay = InternalFocusDisplay()

}
