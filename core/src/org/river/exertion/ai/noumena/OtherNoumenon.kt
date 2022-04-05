package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.AttributeRange

object OtherNoumenon : INoumenon {

    override fun type() = NoumenonType.OTHER
    override fun types() = listOf(type())
    override fun attributeRange() : List<AttributeRange<*>> = listOf()
}