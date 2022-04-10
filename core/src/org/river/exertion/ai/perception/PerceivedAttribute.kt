package org.river.exertion.ai.perception

import org.river.exertion.ai.attribute.AttributeInstance

data class PerceivedAttribute(var attributeInstance : AttributeInstance<*>? = null, var perceivedExternalPhenomena: PerceivedExternalPhenomena? = null)