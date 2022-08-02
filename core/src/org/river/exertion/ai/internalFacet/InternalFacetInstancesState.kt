package org.river.exertion.ai.internalFacet

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFacet.NoneFacet.noneFacet
import org.river.exertion.ai.manifest.IManifest
import org.river.exertion.ai.messaging.FacetMessage
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance
import kotlin.math.roundToInt

class InternalFacetInstancesState(val entity : Telegraph, var internalState: MutableSet<InternalFacetInstance> = mutableSetOf(), var internalAttributes: Set<InternalFacetAttribute> = mutableSetOf()) : Telegraph {

    init {
        MessageChannel.INT_FACET_MODIFY.enableReceive(this)
    }

    var arisingInternalState = InternalFacetAttributesState(entity, internalAttributes)

    fun currentState() : MutableSet<InternalFacetInstance> {

        val returnCurrentState = mutableSetOf<InternalFacetInstance>()

        arisingInternalState.baseline().forEach {
            baselineStateInstance -> returnCurrentState.add(baselineStateInstance)
            if (!internalState.map { it.facetObj }.contains(baselineStateInstance.facetObj) )
                this.internalState.add(InternalFacetInstance(facetObj = baselineStateInstance.facetObj, magnitude = 0f))
        }

        internalState.forEach { internalStateInstance ->
            val baselineInstance = returnCurrentState.firstOrNull { it.facetObj == internalStateInstance.facetObj }

            if ( baselineInstance == null) {
                returnCurrentState.add(internalStateInstance)
            }
            else
                if (internalStateInstance.magnitude > baselineInstance.magnitude) {
                    returnCurrentState.first { it.facetObj == internalStateInstance.facetObj }.magnitude = internalStateInstance.magnitude
                }
        }

        return returnCurrentState
    }

    fun projections() : MutableList<InternalPhenomenaImpression?> {

        val returnProjectionList = MutableList<InternalPhenomenaImpression?>(IManifest.listMax) { null }

        val currentState = currentState()

        if (currentState.isNotEmpty() ) {

            val magnitudeSum = currentState.map { it.magnitude }.reduce { acc, mag -> acc + mag }
            val slots = arisingInternalState.mIntAnxiety * 10
            val spread = if (slots > 0f) magnitudeSum / slots else 1f

            //facet to slots to fill
            val facetSlots = currentState.map { internalFacetInstance ->
                internalFacetInstance to (internalFacetInstance.magnitude / spread).roundToInt()
            }.filter { it.second > 0 }.sortedBy { it.second }

            var slotIdx = 0
            var facetIdx = 0
            var facetSlotCount : Int

            while ((slotIdx < IManifest.listMax) && (facetIdx < facetSlots.size)) {
                facetSlotCount = 0

                while (facetSlotCount < facetSlots[facetIdx].second) {
                    returnProjectionList[slotIdx] = InternalPhenomenaInstance().apply { this.arisenFacet = facetSlots[facetIdx].first }.internalImpression()
                    facetSlotCount++
                    slotIdx++
                }

                facetIdx++
            }
        }
        return returnProjectionList
    }

    fun magnitudeOpinion() : InternalFacetInstance = if (internalState.isEmpty()) noneFacet {} else internalState.maxByOrNull { it.magnitude }!!

    fun add(facet: InternalFacetInstance) {
        this.internalState = (InternalFacetInstancesState(entity, this.internalState) + InternalFacetInstancesState(entity, internalState = mutableSetOf(facet))).internalState
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

        return InternalFacetInstancesState(entity, mergeState)
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

        return InternalFacetInstancesState(entity, mergeState)
    }

    operator fun div(scalar: Float) : InternalFacetInstancesState {

        val returnInstance = InternalFacetInstancesState(entity, this.internalState)
        returnInstance.internalState.forEach { it / scalar }

        return returnInstance
    }

    operator fun times(scalar: Float) : InternalFacetInstancesState {

        val returnInstance = InternalFacetInstancesState(entity, this.internalState)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InternalFacetInstancesState

        if (internalState != other.internalState) return false

        return true
    }

    override fun hashCode(): Int {
        return internalState.hashCode()
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageChannel.INT_FACET_MODIFY.id()) {
                val facetMessage : FacetMessage = MessageChannel.INT_FACET_MODIFY.receiveMessage(msg.extraInfo)

                if (facetMessage.internalFacets != null) {
                    this.internalState = facetMessage.internalFacets!!
                }
            }
        }
        return true
    }

    companion object {

        //TODO: shore up merge to average of facets, grouped by facet type
        fun Set<InternalFacetInstancesState>.merge(entity : Telegraph) : InternalFacetInstancesState {

            val divSize = this.size.toFloat()
            var returnInstance = InternalFacetInstancesState(entity)

            this.forEach { returnInstance += it }

            returnInstance /= divSize

            return returnInstance
        }

    }

}

