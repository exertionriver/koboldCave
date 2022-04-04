package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.AttributeRange
import org.river.exertion.ai.attributes.AttributeRange.Companion.mergeOverrideAttributeRanges
import org.river.exertion.ai.attributes.IntelligenceAttribute
import org.river.exertion.ai.attributes.InternalStateAttribute

object LowRaceNoumenon : INoumenon {

    override fun tag() = "low race"
    override fun tags() = HumanoidNoumenon.tags().toMutableList().apply { this.add( tag() ) }.toList()
    override fun attributeRange() = HumanoidNoumenon.attributeRange().mergeOverrideAttributeRanges(listOf(
        AttributeRange(InternalStateAttribute::class.java, 3, 0.4f, 0.6f),
        AttributeRange(IntelligenceAttribute::class.java, 8, 6, 8)
    ))
}