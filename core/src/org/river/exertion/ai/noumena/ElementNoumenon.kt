package org.river.exertion.ai.noumena

import org.river.exertion.ai.noumena.core.INoumenon
import org.river.exertion.ai.noumena.core.IPropertyable
import org.river.exertion.ai.noumena.core.NoumenonType
import org.river.exertion.ai.property.Quality

object ElementNoumenon : INoumenon, IPropertyable {

    override fun type() = NoumenonType.OTHER
    override fun types() = listOf(type())
    override fun qualities() : List<Quality<*>> = listOf()
}