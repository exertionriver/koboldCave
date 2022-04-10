package org.river.exertion.ai.noumena

import org.river.exertion.ai.attribute.Characteristic
import org.river.exertion.ai.property.Quality

interface INoumenon : IAttributeable, IPropertyable {

    fun type() : NoumenonType
    fun types() : List<NoumenonType>
    override fun characteristics() : List<Characteristic<*>> = listOf()
    override fun qualities() : List<Quality<*>> = listOf()
}