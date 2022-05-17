package org.river.exertion.ai.internalState

import org.river.exertion.ai.internalFacet.InternalFacetAttribute
import org.river.exertion.ai.manifest.IManifest
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance
import kotlin.math.roundToInt

data class InternalFacetAttributesState(var internalFacetAttributes: MutableSet<InternalFacetAttribute> = mutableSetOf()) {

    fun projections(mInternalAnxiety : Float) : MutableList<InternalPhenomenaImpression?> {

        val returnProjectionList = MutableList<InternalPhenomenaImpression?>(IManifest.listMax) { null }

        val magnitudeSum = internalFacetAttributes.map { it.magnitude(mInternalAnxiety) }.reduce { acc, mag -> acc + mag }
        val slots = mInternalAnxiety * 10
        val spread = if (slots > 0f) magnitudeSum / slots else 1f

        //facet to slots to fill
        val facetSlots = internalFacetAttributes.mapIndexed { index, internalFacetAttribute ->
            internalFacetAttribute.arisenFacetInstance(mInternalAnxiety) to (internalFacetAttribute.magnitude(mInternalAnxiety) / spread).roundToInt()
        }.filter { it.second > 0 }.sortedBy { it.second }

        var slotIdx = 0
        var facetIdx = 0
        var facetSlotCount = 0

        while ( (slotIdx < IManifest.listMax) && (facetIdx < facetSlots.size) ) {
            facetSlotCount = 0

            while (facetSlotCount < facetSlots[facetIdx].second) {
                returnProjectionList[slotIdx] = InternalPhenomenaInstance().apply { this.arisenFacet = facetSlots[facetIdx].first }.impression()
                facetSlotCount++
                slotIdx++
            }

            facetIdx++
        }

        return returnProjectionList
    }
}

