package org.river.exertion.ai.noumena

import org.river.exertion.ai.attribute.Trait
import org.river.exertion.ai.symbol.ISymbology

interface IAttributeable {

    fun traits() : List<Trait<*>>
}