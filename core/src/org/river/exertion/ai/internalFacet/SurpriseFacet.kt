package org.river.exertion.ai.internalFacet

object SurpriseFacet : IInternalFacet {

    override val type = InternalFacetType.SURPRISE

    fun surpriseFacet(lambda : InternalFacetInstance.() -> Unit) = InternalFacetInstance(facetObj = SurpriseFacet.javaClass).apply(lambda)

}