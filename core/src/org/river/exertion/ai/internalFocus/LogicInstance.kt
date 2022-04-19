package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.symbol.SymbolType

data class LogicInstance(override var resultSymbolType : SymbolType = SymbolType.NONE
                        , override var logicType : ILogic.LogicType = ILogic.LogicType.NONE
                        , override var op1SymbolType : SymbolType = SymbolType.NONE
                        , override var op2SymbolType : SymbolType = SymbolType.NONE) : ILogic {

    override var impactors = mutableSetOf<InternalFocusImpactor>()

}