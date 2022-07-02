package org.river.exertion.ai.internalSymbol.core

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.InternalFocusDisplay

interface IInternalSymbology {

    var internalSymbolLexicon : MutableSet<ISymbol> //symbols projected over environment or 'motherAI' presents these
    var internalFocusesLexicon : MutableSet<IInternalFocus> //'child AI' uses these to formulate 'response' to symbols

    var internalSymbolDisplay : InternalSymbolDisplay
    var internalFocusDisplay : InternalFocusDisplay

}