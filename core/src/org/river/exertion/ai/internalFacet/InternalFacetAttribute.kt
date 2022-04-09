package org.river.exertion.ai.internalFacet

import org.river.exertion.ai.internalFacet.NoneFacet.noneFacet

data class InternalFacetAttribute(var internalFacetInstance: InternalFacetInstance = noneFacet {}, var origin : Float = 0f, var arising : Float = 0f) {

    fun magnitude(mInternalAnxiety : Float) = origin + (arising - origin) * mInternalAnxiety

    fun arisenFacetInstance(mInternalAnxiety : Float) = InternalFacetInstance(this.internalFacetInstance.facetObj, magnitude(mInternalAnxiety))

    companion object {
        fun internalFacetAttribute(lambda : InternalFacetAttribute.() -> Unit) = InternalFacetAttribute().apply(lambda)
    }

}