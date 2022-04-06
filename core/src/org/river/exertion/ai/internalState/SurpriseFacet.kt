package org.river.exertion.ai.internalState

import org.river.exertion.ai.activities.ContendActivity

object SurpriseFacet : IInternalFacet {

    override val type = InternalFacetType.SURPRISE

    fun surpriseFacet(lambda : InternalFacetInstance.() -> Unit) = InternalFacetInstance(facetObj = SurpriseFacet.javaClass).apply(lambda)

}