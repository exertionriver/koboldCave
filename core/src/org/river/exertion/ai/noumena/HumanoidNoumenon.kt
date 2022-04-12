package org.river.exertion.ai.noumena

import org.river.exertion.ai.attribute.Trait.Companion.mergeOverrideTraits

object HumanoidNoumenon : INoumenon, IAttributeable {

    override fun type() = NoumenonType.HUMANOID
    override fun types() = BeingNoumenon.types().toMutableList().apply { this.add(type()) }.toList()
    override fun traits() = BeingNoumenon.traits().mergeOverrideTraits(listOf())
}