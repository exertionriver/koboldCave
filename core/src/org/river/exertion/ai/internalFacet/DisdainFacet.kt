package org.river.exertion.ai.internalFacet

object DisdainFacet : IInternalFacet {

    override val type = InternalFacetType.DISDAIN

    fun disdainFacet(lambda : InternalFacetInstance.() -> Unit) = InternalFacetInstance(facetObj = DisdainFacet).apply(lambda)

    override fun spawn() = disdainFacet {}

}