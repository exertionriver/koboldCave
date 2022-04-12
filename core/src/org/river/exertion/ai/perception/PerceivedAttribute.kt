package org.river.exertion.ai.perception

import org.river.exertion.ai.attribute.Characteristic

data class PerceivedAttribute(var attributeInstance : Characteristic<*>? = null, var perceivedExternalPhenomena: PerceivedExternalPhenomena? = null)