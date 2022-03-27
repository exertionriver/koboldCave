package org.river.exertion.ai.manifest

import org.river.exertion.ai.phenomena.ExternalPhenomenaImpression
import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression

class ManifestInstance : IManifest {

    override val listMax = 10

    override var manifestType = ExternalPhenomenaType.NONE

    override val perceptionList = MutableList<ExternalPhenomenaImpression?>(listMax) { null }
    override val projectionList = MutableList<InternalPhenomenaImpression?>(listMax) { null }

}