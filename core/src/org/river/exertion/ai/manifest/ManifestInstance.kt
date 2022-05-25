package org.river.exertion.ai.manifest

import org.river.exertion.ai.perception.PerceivedExternalPhenomena
import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression

class ManifestInstance : IManifest {

    override var manifestType = ExternalPhenomenaType.NONE

    override val perceptionList = MutableList<PerceivedExternalPhenomena?>(listMax()) { null }
    override var projectionList = MutableList<InternalPhenomenaImpression?>(listMax()) { null }

}