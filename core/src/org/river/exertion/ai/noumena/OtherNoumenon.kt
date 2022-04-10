package org.river.exertion.ai.noumena

import org.river.exertion.ai.attribute.Characteristic
import org.river.exertion.ai.property.Quality

object OtherNoumenon : INoumenon {

    override fun type() = NoumenonType.OTHER
    override fun types() = listOf(type())
    override fun characteristics() : List<Characteristic<*>> = listOf()
}