package org.river.exertion.ai.internalFacet

object DoubtFacet : IInternalFacet {

    override val type = InternalFacetType.DOUBT

    fun doubtFacet(lambda : InternalFacetInstance.() -> Unit) = InternalFacetInstance(facetObj = DoubtFacet).apply(lambda)

    override fun spawn() = doubtFacet {}

}