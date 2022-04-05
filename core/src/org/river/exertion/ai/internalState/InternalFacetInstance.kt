package org.river.exertion.ai.internalState

data class InternalFacetInstance(var facetObj: Class<IInternalFacet> = (NoneFacet as IInternalFacet).javaClass, var magnitude : Float = 0f) {

    fun facet() : IInternalFacet = facetObj.kotlin.objectInstance!!

    operator fun plus(other : InternalFacetInstance) : Set<InternalFacetInstance> =
            if (this.facetObj == other.facetObj)
                setOf(this.apply { this.magnitude += other.magnitude } )
            else
                setOf(this, other)

    operator fun minus(other : InternalFacetInstance) : Set<InternalFacetInstance> =
            if (this.facetObj == other.facetObj) {
                val minVal = this.magnitude - other.magnitude
                if
                    (minVal > 0) setOf(this.apply { magnitude = minVal } )
                else
                    setOf(this.apply { magnitude = 0f } )
            }
            else
                setOf(this, other)

    companion object {
        fun Pair<InternalFacetInstance, InternalFacetInstance>.avgBy(denom : Int) : InternalFacetInstance =
                InternalFacetInstance(this.first.facetObj, (this@avgBy.first.magnitude + this@avgBy.second.magnitude) / denom)
    }
}