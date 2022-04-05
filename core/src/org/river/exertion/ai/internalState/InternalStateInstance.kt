package org.river.exertion.ai.internalState

import org.river.exertion.ai.internalState.InternalFacetInstance.Companion.avgBy
import org.river.exertion.ai.internalState.NoneFacet.noneFacet

data class InternalStateInstance(var internalState: MutableSet<InternalFacetInstance> = mutableSetOf()) {

    fun magnitudeOpinion() : InternalFacetInstance = if (internalState.isEmpty()) noneFacet {} else internalState.maxByOrNull { it.magnitude }!!

    fun mergeAvg(other: InternalStateInstance, avgBy : Int) {

        val mergeState: MutableSet<InternalFacetInstance> = mutableSetOf()

        val thisFacets = this.internalState.map { it.facetObj }
        val otherFacets = other.internalState.map { it.facetObj }

        this.internalState.filter { otherFacets.contains(it.facetObj) }.forEach { thisSharedFacet ->
            val otherSharedFacet = other.internalState.filter { it.facetObj == thisSharedFacet.facetObj }.first()
            mergeState.add(Pair(thisSharedFacet, otherSharedFacet).avgBy(avgBy))
        }

        mergeState.addAll( this.internalState.filter { otherFacets.contains(it.facetObj) } )
        mergeState.addAll( this.internalState.filter { !otherFacets.contains(it.facetObj) } )
        mergeState.addAll( other.internalState.filter { !thisFacets.contains(it.facetObj) } )

        this.internalState = mergeState
    }

}

