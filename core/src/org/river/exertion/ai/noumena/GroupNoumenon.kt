package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.AttributeRange

object GroupNoumenon : INoumenon {

    override fun tag() = "group"
    override fun tags() = listOf(tag())
    override fun attributeRange() : List<AttributeRange<*>> = listOf()
}