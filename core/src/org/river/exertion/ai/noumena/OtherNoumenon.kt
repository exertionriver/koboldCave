package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.AttributeRange

object OtherNoumenon : INoumenon {

    override fun tag() = "other"
    override fun tags() = listOf(tag())
    override fun attributeRange() : List<AttributeRange<*>> = listOf()
}