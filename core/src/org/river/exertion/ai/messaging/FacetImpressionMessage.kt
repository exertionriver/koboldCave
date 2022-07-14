package org.river.exertion.ai.messaging

import org.river.exertion.ai.phenomena.InternalPhenomenaImpression

data class FacetImpressionMessage(var internalFacetImpressions: MutableList<InternalPhenomenaImpression?> = mutableListOf())