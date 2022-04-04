package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.AttributeRange.Companion.mergeOverrideAttributeRanges

object BeingNoumenon : INoumenon {

    override fun tag() = "being"
    override fun tags() = OtherNoumenon.tags().toMutableList().apply { this.add(tag()) }.toList()
    override fun attributeRange() = OtherNoumenon.attributeRange().mergeOverrideAttributeRanges(listOf())
}