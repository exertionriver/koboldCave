package org.river.exertion.ai.noumena.other.being.fungus.mushroom

import org.river.exertion.ai.attribute.Trait.Companion.mergeOverrideTraits
import org.river.exertion.ai.noumena.core.IAttributeable
import org.river.exertion.ai.noumena.core.INoumenon
import org.river.exertion.ai.noumena.core.InstantiatableNoumenon
import org.river.exertion.ai.noumena.core.NoumenonType
import org.river.exertion.ai.noumena.other.BeingNoumenon

object MushroomNoumenon : INoumenon, InstantiatableNoumenon, IAttributeable {

    override fun type() = NoumenonType.MUSHROOM
    override fun types() = BeingNoumenon.types().toMutableList().apply { this.add(type()) }.toList()
    override fun traits() = BeingNoumenon.traits().mergeOverrideTraits(listOf())
}