package org.river.exertion.ai.perception

import org.river.exertion.ai.phenomena.InternalPhenomenaImpression

data class PerceivedPhenomena(val perceivedExternalPhenomena: PerceivedExternalPhenomena? = null, val internalPhenomenaImpression: InternalPhenomenaImpression? = null)
