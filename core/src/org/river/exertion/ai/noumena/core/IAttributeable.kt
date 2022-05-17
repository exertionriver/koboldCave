package org.river.exertion.ai.noumena.core

import org.river.exertion.ai.attribute.Trait

interface IAttributeable {

    fun traits() : List<Trait<*>>
}