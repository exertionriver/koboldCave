package org.river.exertion.ai.noumena.core

import org.river.exertion.ai.attribute.Trait
import org.river.exertion.ai.internalFacet.InternalFacetAttribute

interface IAttributeable {

    fun traits() : List<Trait<*>>
    fun facetAttributes() : Set<InternalFacetAttribute>

}