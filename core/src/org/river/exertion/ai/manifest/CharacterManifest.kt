package org.river.exertion.ai.manifest

import org.river.exertion.ai.phenomena.ExternalPhenomenaImpression
import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression

class CharacterManifest {

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

    fun update(delta : Float) {
        manifests.forEach { it.perceptionList.forEach { if (it != null) it.countdown -= delta } }
        manifests.forEach { it.projectionList.forEach { if (it != null) it.countdown -= delta } }
    }

    fun addImpression(externalPhenomenaImpression: ExternalPhenomenaImpression) = manifests.filter { it.manifestType == externalPhenomenaImpression.type }.first().addImpression(externalPhenomenaImpression)
    fun addImpression(internalPhenomenaImpression: InternalPhenomenaImpression) = manifests.forEach { it.addImpression(internalPhenomenaImpression) }

    fun getManifest(externalPhenomenaType: ExternalPhenomenaType) = manifests.filter { it.manifestType == externalPhenomenaType }.first()
}