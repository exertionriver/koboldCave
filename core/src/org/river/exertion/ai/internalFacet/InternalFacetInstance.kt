package org.river.exertion.ai.internalFacet

data class InternalFacetInstance(var facetObj: IInternalFacet = NoneFacet, var magnitude : Float = 0f) {

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

    fun description() = "${FacetMagnitudeType.byMagnitude(magnitude).description()} ${facetObj.type.description()}"

}