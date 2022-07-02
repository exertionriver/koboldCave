package org.river.exertion.ai.internalFacet

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.manifest.IManifest
import org.river.exertion.ai.messaging.FacetMessage
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance
import org.river.exertion.ecs.entity.IEntity
import kotlin.math.roundToInt

class InternalFacetAttributesState(val entity : Telegraph, var internalFacetAttributes: Set<InternalFacetAttribute>) : Telegraph  {

    init {
        MessageManager.getInstance().addListener(this, MessageChannel.INT_CONDITION.id())
    }

    var mIntAnxiety = 0f

    fun baseline() : MutableSet<InternalFacetInstance> {

        val returnBaselineSet = mutableSetOf<InternalFacetInstance>()

        internalFacetAttributes.forEach { internalFacetAttribute ->
            returnBaselineSet.add(internalFacetAttribute.arisenFacetInstance(mIntAnxiety))
        }

        return returnBaselineSet
    }

    fun projections() : MutableList<InternalPhenomenaImpression?> {

        val returnProjectionList = MutableList<InternalPhenomenaImpression?>(IManifest.listMax) { null }

        if (internalFacetAttributes.isNotEmpty() ) {

            val magnitudeSum = internalFacetAttributes.map { it.magnitude(mIntAnxiety) }.reduce { acc, mag -> acc + mag }
            val slots = mIntAnxiety * 10
            val spread = if (slots > 0f) magnitudeSum / slots else 1f

            //facet to slots to fill
            val facetSlots = internalFacetAttributes.mapIndexed { index, internalFacetAttribute ->
                internalFacetAttribute.arisenFacetInstance(mIntAnxiety) to (internalFacetAttribute.magnitude(mIntAnxiety) / spread).roundToInt()
            }.filter { it.second > 0 }.sortedBy { it.second }

            var slotIdx = 0
            var facetIdx = 0
            var facetSlotCount = 0

            while ((slotIdx < IManifest.listMax) && (facetIdx < facetSlots.size)) {
                facetSlotCount = 0

                while (facetSlotCount < facetSlots[facetIdx].second) {
                    returnProjectionList[slotIdx] = InternalPhenomenaInstance().apply { this.arisenFacet = facetSlots[facetIdx].first }.impression()
                    facetSlotCount++
                    slotIdx++
                }

                facetIdx++
            }
        }
        return returnProjectionList
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageChannel.INT_CONDITION.id()) {
                this.mIntAnxiety = msg.extraInfo as Float
            }
        }
        return true
    }
}

