package org.river.exertion.ai.internalFacet

object DisgustFacet : IInternalFacet {

    override val type = InternalFacetType.DISGUST

    fun disgustFacet(lambda : InternalFacetInstance.() -> Unit) = InternalFacetInstance(facetObj = DisgustFacet).apply(lambda)

    override fun spawn() = disgustFacet {}

}