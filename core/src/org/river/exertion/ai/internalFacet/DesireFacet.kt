package org.river.exertion.ai.internalFacet

object DesireFacet : IInternalFacet {

    override val type = InternalFacetType.DESIRE

    fun desireFacet(lambda : InternalFacetInstance.() -> Unit) = InternalFacetInstance(facetObj = DesireFacet).apply(lambda)

    override fun spawn() = desireFacet {}

}