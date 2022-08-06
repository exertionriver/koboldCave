package org.river.exertion.ai.perception

import org.river.exertion.ai.attribute.Characteristic
import org.river.exertion.ai.memory.KnowledgeSourceInstance

data class PerceivedAttribute(var attributeInstance : Characteristic<*>? = null, var perceivedExternalPhenomena: PerceivedExternalPhenomena? = null, var knowledgeSourceInstance: KnowledgeSourceInstance = KnowledgeSourceInstance())