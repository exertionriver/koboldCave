package org.river.exertion.ai.internalFacet

object NoneFacet : IInternalFacet {

    override val type = InternalFacetType.NONE

    fun noneFacet(lambda : () -> Unit) = InternalFacetInstance(facetObj = NoneFacet)

    override fun spawn() = noneFacet {}
}