package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.AttributeRange.Companion.mergeOverrideAttributeRanges

object HumanoidNoumenon : INoumenon {

    override fun tag() = "humanoid"
    override fun tags() = BeingNoumenon.tags().toMutableList().apply { this.add(tag()) }.toList()
    override fun attributeRange() = BeingNoumenon.attributeRange().mergeOverrideAttributeRanges(listOf())
}