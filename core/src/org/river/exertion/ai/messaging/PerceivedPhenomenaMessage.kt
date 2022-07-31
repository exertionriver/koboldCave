package org.river.exertion.ai.messaging

import org.river.exertion.ai.perception.PerceivedPhenomena

data class PerceivedPhenomenaMessage(var perceivedPhenomena: MutableList<PerceivedPhenomena>? = null)