package org.river.exertion.ai.memory

data class KnowledgeSourceInstance(var source : KnowledgeSourceType = KnowledgeSourceType.NONE, var trust : Float = 0.5f)