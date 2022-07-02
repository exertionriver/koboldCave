package org.river.exertion.ai.internalFacet

object SurpriseFacet : IInternalFacet {

    override val type = InternalFacetType.SURPRISE

    fun surpriseFacet(lambda : InternalFacetInstance.() -> Unit) = InternalFacetInstance(facetObj = SurpriseFacet).apply(lambda)

    override fun spawn() = surpriseFacet {}
}