package org.river.exertion.ai.noumena

import org.river.exertion.ai.attribute.Trait
import org.river.exertion.ai.internalFacet.InternalFacetAttribute
import org.river.exertion.ai.noumena.core.IAttributeable
import org.river.exertion.ai.noumena.core.INoumenon
import org.river.exertion.ai.noumena.core.NoumenonType

object OtherNoumenon : INoumenon, IAttributeable {

    override fun type() = NoumenonType.OTHER
    override fun types() = listOf(type())
    override fun traits() : List<Trait<*>> = listOf()
    override fun facetAttributes() = mutableSetOf<InternalFacetAttribute>()
}