package org.river.exertion.ai.manifest

import org.river.exertion.ai.phenomena.ExternalPhenomenaImpression
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression
import org.river.exertion.btree.v0_1.IBTCharacter

data class PerceivedJoinedPhenomena(val sender : IBTCharacter? = null, val externalPhenomenaImpression: ExternalPhenomenaImpression? = null, val internalPhenomenaImpression: InternalPhenomenaImpression? = null)
