package org.river.exertion.ai.internalFacet

data class InternalFacetAttribute(var facetObj: Class<IInternalFacet> = (NoneFacet as IInternalFacet).javaClass, var origin : Float = 0f, var arising : Float = 0f) {

    fun magnitude(mInternalAnxiety : Float) = origin + (arising - origin) * mInternalAnxiety

    fun arisenFacetInstance(mInternalAnxiety : Float) = InternalFacetInstance(this.facetObj, magnitude(mInternalAnxiety))

    companion object {
        fun internalFacetAttribute(lambda : InternalFacetAttribute.() -> Unit) = InternalFacetAttribute().apply(lambda)
    }

}