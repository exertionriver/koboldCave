package org.river.exertion.ai.manifest

import org.river.exertion.ai.perception.PerceivedExternalPhenomena
import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression

class ManifestInstance : IManifest {

    override val listMax = 10

    override var manifestType = ExternalPhenomenaType.NONE

    override val perceptionList = MutableList<PerceivedExternalPhenomena?>(listMax) { null }
    override val projectionList = MutableList<InternalPhenomenaImpression?>(listMax) { null }

}