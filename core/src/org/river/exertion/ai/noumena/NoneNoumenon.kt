package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.AttributeRange

object NoneNoumenon : INoumenon {

    override fun type() = NoumenonType.NONE
    override fun types() = listOf(type())
    override fun attributeRange() : List<AttributeRange<*>> = listOf()
}