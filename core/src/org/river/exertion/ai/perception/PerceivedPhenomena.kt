package org.river.exertion.ai.perception

import org.river.exertion.ai.phenomena.InternalPhenomenaImpression

data class PerceivedPhenomena(var perceivedExternalPhenomena: PerceivedExternalPhenomena? = null, var internalPhenomenaImpression: InternalPhenomenaImpression? = null)
