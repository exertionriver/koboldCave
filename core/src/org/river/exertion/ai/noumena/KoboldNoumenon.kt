package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.*
import org.river.exertion.ai.attributes.AttributeRange.Companion.mergeOverrideAttributeRanges

object KoboldNoumenon : INoumenon {

    override fun tag() = "kobold"
    override fun tags() = LowRaceNoumenon.tags().toMutableList().apply { this.add(tag()) }.toList()
    override fun attributeRange() = LowRaceNoumenon.attributeRange().mergeOverrideAttributeRanges( listOf(
        AttributeRange(GrowlAttribute::class.java, 2, tag(), tag()),
        AttributeRange(InternalStateAttribute::class.java, 3, 0.5f, 0.6f),
        AttributeRange(IntelligenceAttribute::class.java, 8, 7, 8)
    ))
}