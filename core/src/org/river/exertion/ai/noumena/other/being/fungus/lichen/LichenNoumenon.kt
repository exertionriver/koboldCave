package org.river.exertion.ai.noumena.other.being.fungus.lichen

import org.river.exertion.ai.attribute.Trait.Companion.mergeOverrideTraits
import org.river.exertion.ai.noumena.IAttributeable
import org.river.exertion.ai.noumena.INoumenon
import org.river.exertion.ai.noumena.InstantiatableNoumenon
import org.river.exertion.ai.noumena.NoumenonType
import org.river.exertion.ai.noumena.other.BeingNoumenon
import org.river.exertion.ai.noumena.other.being.FungusNoumenon

object LichenNoumenon : INoumenon, InstantiatableNoumenon, IAttributeable {

    override fun type() = NoumenonType.LICHEN
    override fun types() = FungusNoumenon.types().toMutableList().apply { this.add(type()) }.toList()
    override fun traits() = FungusNoumenon.traits().mergeOverrideTraits(listOf())
}