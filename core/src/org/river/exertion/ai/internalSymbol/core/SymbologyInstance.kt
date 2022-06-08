package org.river.exertion.ai.internalSymbol.core;

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.InternalFocusDisplay

class SymbologyInstance(val entity : Telegraph, val initSymbology : IInternalSymbology? = null) : IInternalSymbology {

    override var internalSymbolLexicon = mutableSetOf<ISymbol>()
    override var internalFocusesLexicon = mutableSetOf<IInternalFocus>()

    override var internalSymbolDisplay = InternalSymbolDisplay(entity)
    override var internalFocusDisplay = InternalFocusDisplay(entity)

}
