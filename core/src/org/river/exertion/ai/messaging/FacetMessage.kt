package org.river.exertion.ai.messaging

import org.river.exertion.ai.internalFacet.InternalFacetInstance

data class FacetMessage(var internalFacets: MutableSet<InternalFacetInstance>? = null)