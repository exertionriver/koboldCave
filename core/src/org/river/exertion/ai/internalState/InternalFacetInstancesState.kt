package org.river.exertion.ai.internalState

import org.river.exertion.ai.internalFacet.InternalFacetInstance
import org.river.exertion.ai.internalFacet.InternalFacetType
import org.river.exertion.ai.internalFacet.NoneFacet.noneFacet

data class InternalFacetInstancesState(var internalState: MutableSet<InternalFacetInstance> = mutableSetOf()) {

    fun magnitudeOpinion() : InternalFacetInstance = if (internalState.isEmpty()) noneFacet {} else internalState.maxByOrNull { it.magnitude }!!

    fun add(facet: InternalFacetInstance) {
        this.internalState = (InternalFacetInstancesState(this.internalState) + InternalFacetInstancesState(internalState = mutableSetOf(facet))).internalState
    }

    operator fun plus(other: InternalFacetInstancesState) : InternalFacetInstancesState {

        val mergeState: MutableSet<InternalFacetInstance> = mutableSetOf()

        val thisFacets = this.internalState.map { it.facetObj }
        val otherFacets = other.internalState.map { it.facetObj }

        //add shared facets together, to mergestate
        this.internalState.filter { otherFacets.contains(it.facetObj) }.forEach { thisSharedFacet ->
            val otherSharedFacet = other.internalState.filter { it.facetObj == thisSharedFacet.facetObj }.first()
            mergeState.add(thisSharedFacet + otherSharedFacet)
        }

        //add facets not shared
        mergeState.addAll( this.internalState.filter { !otherFacets.contains(it.facetObj) } )
        mergeState.addAll( other.internalState.filter { !thisFacets.contains(it.facetObj) } )

        return InternalFacetInstancesState(mergeState)
    }

    operator fun minus(other: InternalFacetInstancesState) : InternalFacetInstancesState {

        val mergeState: MutableSet<InternalFacetInstance> = mutableSetOf()

        val thisFacets = this.internalState.map { it.facetObj }
        val otherFacets = other.internalState.map { it.facetObj }

        //add shared facets together, to mergestate
        this.internalState.filter { otherFacets.contains(it.facetObj) }.forEach { thisSharedFacet ->
            val otherSharedFacet = other.internalState.filter { it.facetObj == thisSharedFacet.facetObj }.first()
            mergeState.add(thisSharedFacet - otherSharedFacet)
        }

        //add facets not shared
        mergeState.addAll( this.internalState.filter { !otherFacets.contains(it.facetObj) } )
        mergeState.addAll( other.internalState.filter { !thisFacets.contains(it.facetObj) } )

        return InternalFacetInstancesState(mergeState)
    }

    operator fun div(scalar: Float) : InternalFacetInstancesState {

        val returnInstance = InternalFacetInstancesState(this.internalState)
        returnInstance.internalState.forEach { it / scalar }

        return returnInstance
    }

    operator fun times(scalar: Float) : InternalFacetInstancesState {

        val returnInstance = InternalFacetInstancesState(this.internalState)
        returnInstance.internalState.forEach { it * scalar }

        return returnInstance
    }

    fun description() =
        when (internalState.size) {
            0 -> InternalFacetType.NONE.description()
            1 -> internalState.first().description()
            else -> {
                var returnString = "a few things:"
                internalState.forEach { returnString += " ${it.description()}" }
                returnString
            }
        }

    companion object {

        fun Set<InternalFacetInstancesState>.merge() : InternalFacetInstancesState {

            val divSize = this.size.toFloat()
            var returnInstance = InternalFacetInstancesState()

            this.forEach { returnInstance += it }

            returnInstance /= divSize

            return returnInstance
        }

    }

}

