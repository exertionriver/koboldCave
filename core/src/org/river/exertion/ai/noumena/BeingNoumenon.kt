package org.river.exertion.ai.noumena

import org.river.exertion.ai.attribute.Trait.Companion.mergeOverrideTraits

object BeingNoumenon : INoumenon, IAttributeable {

    override fun type() = NoumenonType.BEING
    override fun types() = OtherNoumenon.types().toMutableList().apply { this.add(type()) }.toList()
    override fun traits() = OtherNoumenon.traits().mergeOverrideTraits(listOf())
}