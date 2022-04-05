package org.river.exertion.ai.internalState

object NoneFacet : IInternalFacet {

    override val type = InternalFacetType.NONE

    fun noneFacet(lambda : () -> Unit) = InternalFacetInstance(facetObj = NoneFacet.javaClass)
}