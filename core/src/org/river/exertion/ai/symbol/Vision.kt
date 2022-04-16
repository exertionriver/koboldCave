package org.river.exertion.ai.symbol

//conviction == 1f, complete conviction; conviction == 0f, loss of conviction
data class Vision(override var type: SymbolType, override var referent: Any, var conviction : Float, var accomplishment : Float) : ISymbol {

    companion object {
        fun Belief.vision(accomplishment: Float) = Vision(this.type, this.referent, this.conviction, accomplishment)
    }
}

