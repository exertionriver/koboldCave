package org.river.exertion.ai.internalState

data class InternalFacetInstance(var facetObj: Class<IInternalFacet> = (NoneFacet as IInternalFacet).javaClass, var magnitude : Float = 0f) {

    fun facet() : IInternalFacet = facetObj.kotlin.objectInstance!!

    operator fun plus(other : InternalFacetInstance) : InternalFacetInstance =
            if (this.facetObj == other.facetObj)
                InternalFacetInstance(this.facetObj, this.magnitude + other.magnitude)
            else this

    operator fun minus(other : InternalFacetInstance) : InternalFacetInstance =
            if (this.facetObj == other.facetObj) {
                val minVal = this.magnitude - other.magnitude
                if
                    (minVal > 0) InternalFacetInstance(this.facetObj, magnitude = minVal)
                else
                    InternalFacetInstance(this.facetObj, magnitude = 0f)
            }
            else this

    operator fun div(scalar : Float) : InternalFacetInstance = InternalFacetInstance(this.facetObj, this.magnitude / scalar)

    operator fun times(scalar : Float) : InternalFacetInstance = InternalFacetInstance(this.facetObj, this.magnitude * scalar)

    fun description() = "${FacetMagnitudeType.byMagnitude(magnitude).description()} ${facet().type.description()}"

}