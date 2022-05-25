package org.river.exertion.ai.manifest

import org.river.exertion.ai.perception.PerceivedExternalPhenomena
import org.river.exertion.ai.perception.PerceivedPhenomena
import org.river.exertion.ai.phenomena.ExternalPhenomenaImpression
import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression
import org.river.exertion.ecs.entity.IEntity

class InternalManifest {

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

    fun addFacetImpressions(facetImpressions : MutableList<InternalPhenomenaImpression?>) = manifests.forEach {manifest -> manifest.projectionList = facetImpressions}

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



}