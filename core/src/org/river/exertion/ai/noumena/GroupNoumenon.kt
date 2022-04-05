package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.AttributeRange

object GroupNoumenon : INoumenon {

    override fun type() = NoumenonType.GROUP
    override fun types() = listOf(type())
    override fun attributeRange() : List<AttributeRange<*>> = listOf()
}