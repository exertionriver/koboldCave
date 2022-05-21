package org.river.exertion.ai.perception

import org.river.exertion.ai.phenomena.ExternalPhenomenaImpression
import org.river.exertion.ecs.entity.IEntity

data class PerceivedExternalPhenomena(val sender : IEntity?, val externalPhenomenaImpression: ExternalPhenomenaImpression?)
