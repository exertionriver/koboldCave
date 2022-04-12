package org.river.exertion.ai.noumena

import org.river.exertion.ai.attribute.Trait

object OtherNoumenon : INoumenon, IAttributeable {

    override fun type() = NoumenonType.OTHER
    override fun types() = listOf(type())
    override fun traits() : List<Trait<*>> = listOf()
}