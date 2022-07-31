package org.river.exertion.ai.manifest

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.messaging.FacetImpressionMessage
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.perception.PerceivedExternalPhenomena
import org.river.exertion.ai.perception.PerceivedPhenomena
import org.river.exertion.ai.phenomena.*
import org.river.exertion.ecs.entity.IEntity

class InternalManifest(val entity : Telegraph) : Telegraph {

    init {
        MessageChannel.ADD_EXT_PHENOMENA.enableReceive(this)
        MessageChannel.ADD_INT_PHENOMENA.enableReceive(this)
        MessageChannel.REMOVE_EXT_PHENOMENA.enableReceive(this)
        MessageChannel.REMOVE_INT_PHENOMENA.enableReceive(this)
        MessageChannel.INT_PHENOMENA_FACETS.enableReceive(this)
    }

    val manifests = mutableListOf(
        ManifestInstance().apply { this.manifestType = ExternalPhenomenaType.VISUAL }
        , ManifestInstance().apply { this.manifestType = ExternalPhenomenaType.AUDITORY }
        , ManifestInstance().apply { this.manifestType = ExternalPhenomenaType.OLFACTORY }
        , ManifestInstance().apply { this.manifestType = ExternalPhenomenaType.GUSTATORY }
        , ManifestInstance().apply { this.manifestType = ExternalPhenomenaType.TACTILE }
        , ManifestInstance().apply { this.manifestType = ExternalPhenomenaType.INTELLIGENCE }
        , ManifestInstance().apply { this.manifestType = ExternalPhenomenaType.WISDOM }
        , ManifestInstance().apply { this.manifestType = ExternalPhenomenaType.EXTRASENSORY }
    )

    fun addImpression(sender : IEntity, externalPhenomenaImpression: ExternalPhenomenaImpression) = manifests.filter { it.manifestType == externalPhenomenaImpression.type }.first().addImpression(PerceivedExternalPhenomena(sender, externalPhenomenaImpression))
    fun addImpression(internalPhenomenaImpression: InternalPhenomenaImpression) = manifests.forEach { it.addImpression(internalPhenomenaImpression) }

    @Suppress("NewApi")
    fun removeImpression(perceivedExternalPhenomena: PerceivedExternalPhenomena) {
        val removeIndex = manifests.filter { it.manifestType == perceivedExternalPhenomena.externalPhenomenaImpression!!.type }.first().perceptionList.indexOf(perceivedExternalPhenomena)
        if (removeIndex >= 0) {
            manifests.filter { it.manifestType == perceivedExternalPhenomena.externalPhenomenaImpression!!.type }.first().perceptionList.removeAt(removeIndex)
            manifests.filter { it.manifestType == perceivedExternalPhenomena.externalPhenomenaImpression!!.type }.first().perceptionList.add(removeIndex, null)
        }
    }

    fun removeImpression(internalPhenomenaImpression: InternalPhenomenaImpression) {
        val removeIndex = manifests.first().projectionList.indexOf(internalPhenomenaImpression)
        if (removeIndex >= 0) {
            manifests.forEach {
                if (it.projectionList[removeIndex] != null) {
                    it.projectionList.removeAt(removeIndex)
                    it.projectionList.add(removeIndex, null)
                }
            }
        }
    }

    fun addFacetImpressions(facetImpressions : MutableList<InternalPhenomenaImpression?>) {
        manifests.first().projectionList.forEachIndexed { idx, internalPhenomenaImpression ->
                if (internalPhenomenaImpression != null && internalPhenomenaImpression.countdown > 0) facetImpressions[idx] = internalPhenomenaImpression
        }
        manifests.forEach {
            it.projectionList = facetImpressions
        }
    }

    fun getManifest(externalPhenomenaType: ExternalPhenomenaType) = manifests.filter { it.manifestType == externalPhenomenaType }.first()

    fun getExternalPhenomenaList() : MutableList<PerceivedExternalPhenomena> {

        val returnList = mutableListOf<PerceivedExternalPhenomena>()

        manifests.forEach { manifest -> manifest.perceptionList.forEach { perceivedExternalPhenomena -> if (perceivedExternalPhenomena != null) returnList.add(perceivedExternalPhenomena) } }

        return returnList
    }

    fun getPerceivedPhenomenaList() : MutableList<PerceivedPhenomena> {

        val returnList = mutableListOf<PerceivedPhenomena>()

        manifests.forEach { manifest -> manifest.joinedList().forEach { perceivedPhenomena -> if (perceivedPhenomena.perceivedExternalPhenomena != null) returnList.add(perceivedPhenomena) } }

        return returnList
    }

    fun pollRandomExternalPhenomena(excludeList : MutableList<PerceivedExternalPhenomena>) : PerceivedExternalPhenomena {
        val fullList = getExternalPhenomenaList()
        fullList.removeAll(excludeList)
        return fullList.apply { this.shuffle() }.first()
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender != entity) ) {
            if (msg.message == MessageChannel.ADD_EXT_PHENOMENA.id()) {
                addImpression(msg.sender as IEntity, (msg.extraInfo as ExternalPhenomenaInstance).impression())
            }
            if (msg.message == MessageChannel.ADD_INT_PHENOMENA.id()) {
                addImpression((msg.extraInfo as InternalPhenomenaInstance).impression())
            }
        }
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageChannel.INT_PHENOMENA_FACETS.id()) {
                addFacetImpressions( (msg.extraInfo as FacetImpressionMessage).internalFacetImpressions)
            }
            if (msg.message == MessageChannel.REMOVE_INT_PHENOMENA.id()) {
                removeImpression(msg.extraInfo as InternalPhenomenaImpression)
            }
            if (msg.message == MessageChannel.REMOVE_EXT_PHENOMENA.id()) {
                removeImpression(msg.extraInfo as PerceivedExternalPhenomena)
            }
        }
        return true
    }

}