package org.river.exertion.ai.perception

import org.river.exertion.ai.attributes.AttributeInstance
import org.river.exertion.ai.attributes.AttributeValue
import org.river.exertion.ai.memory.KnowledgeSourceInstance
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance

data class PerceivedAttribute(var attributeInstance : AttributeInstance<*>? = null, var perceivedExternalPhenomena: PerceivedExternalPhenomena? = null)