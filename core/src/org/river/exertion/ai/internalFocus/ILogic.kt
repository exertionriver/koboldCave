package org.river.exertion.ai.internalFocus

interface ILogic : IInternalFocusTypeInstance {

    enum class LogicType {
        AND { override fun tag() = "and" },
        OR { override fun tag() = "or" },
        NOT { override fun tag() = "not" },
        NONE
        ;
        open fun tag() : String = "none"
    }

//    var resultSymbolType : SymbolType
    var logicType: LogicType
//    var op1SymbolType : SymbolType
//    var op2SymbolType : SymbolType
}


