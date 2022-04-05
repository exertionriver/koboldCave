package org.river.exertion.ai.internalState

import org.river.exertion.ai.internalState.NoneFacet.noneFacet

data class InternalStateInstance(var internalState: MutableSet<InternalFacetInstance> = mutableSetOf()) {

    fun magnitudeOpinion() : InternalFacetInstance = if (internalState.isEmpty()) noneFacet {} else internalState.maxByOrNull { it.magnitude }!!

    operator fun plus(other: InternalStateInstance) : InternalStateInstance {

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

        return InternalStateInstance(mergeState)
    }

    operator fun minus(other: InternalStateInstance) : InternalStateInstance {

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

        return InternalStateInstance(mergeState)
    }

    operator fun div(scalar: Float) : InternalStateInstance {

        val returnInstance = InternalStateInstance(this.internalState)
        returnInstance.internalState.forEach { it / scalar }

        return returnInstance
    }

    operator fun times(scalar: Float) : InternalStateInstance {

        val returnInstance = InternalStateInstance(this.internalState)
        returnInstance.internalState.forEach { it * scalar }

        return returnInstance
    }

    fun description() =
        when (internalState.size) {
            0 -> InternalFacetType.NONE.description()
            1 -> internalState.first().description()
            else -> "a few things:" + internalState.forEach { " ${it.description()}" }
        }

    companion object {

        fun Set<InternalStateInstance>.merge() : InternalStateInstance {

            val divSize = this.size.toFloat()
            var returnInstance = InternalStateInstance()

            this.forEach { returnInstance += it }

            returnInstance /= divSize

            return returnInstance
        }

    }

}

