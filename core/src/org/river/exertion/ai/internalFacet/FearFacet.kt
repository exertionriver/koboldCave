package org.river.exertion.ai.internalFacet

object FearFacet : IInternalFacet {

    override val type = InternalFacetType.FEAR

    fun fearFacet(lambda : InternalFacetInstance.() -> Unit) = InternalFacetInstance(facetObj = FearFacet).apply(lambda)

    override fun spawn() = fearFacet {}
}