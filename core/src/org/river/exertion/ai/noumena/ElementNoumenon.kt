package org.river.exertion.ai.noumena

import org.river.exertion.ai.property.Quality

object ElementNoumenon : INoumenon, IPropertyable {

    override fun type() = NoumenonType.OTHER
    override fun types() = listOf(type())
    override fun qualities() : List<Quality<*>> = listOf()
}