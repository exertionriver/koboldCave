package org.river.exertion.ai.internalFacet

object ConfusionFacet : IInternalFacet {

    override val type = InternalFacetType.CONFUSION

    fun confusionFacet(lambda : InternalFacetInstance.() -> Unit) = InternalFacetInstance(facetObj = ConfusionFacet.javaClass).apply(lambda)

}