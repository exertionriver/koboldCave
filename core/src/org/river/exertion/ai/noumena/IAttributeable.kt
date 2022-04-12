package org.river.exertion.ai.noumena

import org.river.exertion.ai.attribute.Trait

interface IAttributeable {

    fun traits() : List<Trait<*>>
}