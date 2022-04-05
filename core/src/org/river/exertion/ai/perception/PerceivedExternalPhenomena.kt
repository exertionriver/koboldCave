package org.river.exertion.ai.perception

import org.river.exertion.ai.phenomena.ExternalPhenomenaImpression
import org.river.exertion.btree.v0_1.IBTCharacter

data class PerceivedExternalPhenomena(val sender : IBTCharacter?, val externalPhenomenaImpression: ExternalPhenomenaImpression?)
