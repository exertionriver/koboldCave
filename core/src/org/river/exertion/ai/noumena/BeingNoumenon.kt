package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.AttributeRange.Companion.mergeOverrideAttributeRanges

object BeingNoumenon : INoumenon {

    override fun type() = NoumenonType.BEING
    override fun types() = OtherNoumenon.types().toMutableList().apply { this.add(type()) }.toList()
    override fun attributeRange() = OtherNoumenon.attributeRange().mergeOverrideAttributeRanges(listOf())
}