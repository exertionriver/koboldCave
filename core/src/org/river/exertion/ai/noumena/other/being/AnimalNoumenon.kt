package org.river.exertion.ai.noumena.other.being

import org.river.exertion.ai.attribute.Trait.Companion.mergeOverrideTraits
import org.river.exertion.ai.internalFacet.InternalFacetAttribute
import org.river.exertion.ai.noumena.core.IAttributeable
import org.river.exertion.ai.noumena.core.INoumenon
import org.river.exertion.ai.noumena.core.NoumenonType
import org.river.exertion.ai.noumena.other.BeingNoumenon

object AnimalNoumenon : INoumenon, IAttributeable {

    override fun type() = NoumenonType.ANIMAL
    override fun types() = BeingNoumenon.types().toMutableList().apply { this.add(type()) }.toList()
    override fun traits() = BeingNoumenon.traits().mergeOverrideTraits(listOf())
    override fun facetAttributes() = mutableSetOf<InternalFacetAttribute>()
}