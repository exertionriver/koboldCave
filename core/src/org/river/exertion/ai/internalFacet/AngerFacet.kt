package org.river.exertion.ai.internalFacet

object AngerFacet : IInternalFacet {

    override val type = InternalFacetType.ANGER

    fun angerFacet(lambda : InternalFacetInstance.() -> Unit) = InternalFacetInstance(facetObj = AngerFacet).apply(lambda)

    override fun spawn() = angerFacet {}
}