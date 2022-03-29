package org.river.exertion.ai.manifest

import org.river.exertion.ai.phenomena.ExternalPhenomenaImpression
import org.river.exertion.btree.v0_1.IBTCharacter

data class PerceivedPhenomena(val sender : IBTCharacter, val externalPhenomenaImpression: ExternalPhenomenaImpression)
