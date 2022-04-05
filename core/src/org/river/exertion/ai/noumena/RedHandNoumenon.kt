package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.AttributeRange.Companion.mergeOverrideAttributeRanges

class RedHandNoumenon : INoumenon {

    override fun type() = NoumenonType.RED_HAND
    override fun types() = GroupNoumenon.types().toMutableList().apply { this.add( type() ) }.toList()
    override fun attributeRange() = GroupNoumenon.attributeRange().mergeOverrideAttributeRanges(listOf())
}