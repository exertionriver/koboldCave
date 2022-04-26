package org.river.exertion.ai.symbol

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.InternalFocusDisplay

interface ISymbology {

    var symbolLexicon : MutableSet<ISymbol> //symbols projected over environment or 'motherAI' presents these
    var internalFocusesLexicon : MutableSet<IInternalFocus> //'child AI' uses these to formulate 'response' to symbols

    var symbolDisplay : SymbolDisplay
    var internalFocusDisplay : InternalFocusDisplay
}