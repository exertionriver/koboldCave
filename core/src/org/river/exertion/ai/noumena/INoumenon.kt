package org.river.exertion.ai.noumena

import org.river.exertion.ai.symbol.ReferentType

interface INoumenon : ReferentType {

    fun type() : NoumenonType
    fun types() : List<NoumenonType>
}