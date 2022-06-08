package org.river.exertion.ai.noumena.other

import org.river.exertion.ai.attribute.Trait.Companion.mergeOverrideTraits
import org.river.exertion.ai.internalFacet.InternalFacetAttribute
import org.river.exertion.ai.noumena.core.IAttributeable
import org.river.exertion.ai.noumena.core.INoumenon
import org.river.exertion.ai.noumena.core.NoumenonType
import org.river.exertion.ai.noumena.OtherNoumenon

object BeingNoumenon : INoumenon, IAttributeable {

    override fun type() = NoumenonType.BEING
    override fun types() = OtherNoumenon.types().toMutableList().apply { this.add(type()) }.toList()
    override fun traits() = OtherNoumenon.traits().mergeOverrideTraits(listOf())
    override fun facetAttributes() = mutableSetOf<InternalFacetAttribute>()
}