package org.river.exertion.ai.manifest

import org.river.exertion.ai.phenomena.ExternalPhenomenaImpression
import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression
import org.river.exertion.btree.v0_1.IBTCharacter

class ManifestInstance : IManifest {

    override val listMax = 10

    override var manifestType = ExternalPhenomenaType.NONE

    override val perceptionList = MutableList<PerceivedPhenomena?>(listMax) { null }
    override val projectionList = MutableList<InternalPhenomenaImpression?>(listMax) { null }

}