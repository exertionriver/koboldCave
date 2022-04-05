package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.AttributeRange.Companion.mergeOverrideAttributeRanges

object HumanoidNoumenon : INoumenon {

    override fun type() = NoumenonType.HUMANOID
    override fun types() = BeingNoumenon.types().toMutableList().apply { this.add(type()) }.toList()
    override fun attributeRange() = BeingNoumenon.attributeRange().mergeOverrideAttributeRanges(listOf())
}