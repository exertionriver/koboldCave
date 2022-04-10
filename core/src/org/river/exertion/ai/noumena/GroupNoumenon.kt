package org.river.exertion.ai.noumena

import org.river.exertion.ai.attribute.Characteristic

object GroupNoumenon : INoumenon {

    override fun type() = NoumenonType.GROUP
    override fun types() = listOf(type())
    override fun characteristics() : List<Characteristic<*>> = listOf()
}