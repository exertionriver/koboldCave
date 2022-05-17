package org.river.exertion.ai.noumena

import org.river.exertion.ai.attribute.Trait
import org.river.exertion.ai.noumena.core.IAttributeable
import org.river.exertion.ai.noumena.core.INoumenon
import org.river.exertion.ai.noumena.core.NoumenonType

object GroupNoumenon : INoumenon, IAttributeable {

    override fun type() = NoumenonType.GROUP
    override fun types() = listOf(type())
    override fun traits() : List<Trait<*>> = listOf()
}