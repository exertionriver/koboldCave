package org.river.exertion.ai

data class Signature(var individual : String, var group : String, var type : String) {

    //attribute / characteristic?
    companion object {
        fun empty() = Signature("none", "none", "none")
    }

}

