package org.river.exertion.ai.noumena.other.being.fungus.mushroom

import org.river.exertion.ai.attribute.Trait.Companion.mergeOverrideTraits
import org.river.exertion.ai.noumena.IAttributeable
import org.river.exertion.ai.noumena.INoumenon
import org.river.exertion.ai.noumena.InstantiatableNoumenon
import org.river.exertion.ai.noumena.NoumenonType
import org.river.exertion.ai.noumena.other.BeingNoumenon

object MushroomNoumenon : INoumenon, InstantiatableNoumenon, IAttributeable {

    override fun type() = NoumenonType.MUSHROOM
    override fun types() = BeingNoumenon.types().toMutableList().apply { this.add(type()) }.toList()
    override fun traits() = BeingNoumenon.traits().mergeOverrideTraits(listOf())
}