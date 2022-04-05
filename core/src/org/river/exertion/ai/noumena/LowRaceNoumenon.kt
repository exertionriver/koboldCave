package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.AttributeRange.Companion.mergeOverrideAttributeRanges
import org.river.exertion.ai.attributes.IntelligenceAttribute.intelligenceRange
import org.river.exertion.ai.attributes.InternalStateAttribute.internalStateRange

object LowRaceNoumenon : INoumenon {

    override fun type() = NoumenonType.LOW_RACE
    override fun types() = HumanoidNoumenon.types().toMutableList().apply { this.add( type() ) }.toList()
    override fun attributeRange() = HumanoidNoumenon.attributeRange().mergeOverrideAttributeRanges(listOf(
        internalStateRange { noumenonObj = this@LowRaceNoumenon.javaClass; noumenonOrder = 3; minValue = 0.4f; maxValue = 0.6f },
        intelligenceRange { noumenonObj = this@LowRaceNoumenon.javaClass; noumenonOrder = 8; minValue = 6; maxValue = 8 }
    ))
}