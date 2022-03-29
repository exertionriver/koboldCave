package org.river.exertion.ai.attributes

import org.river.exertion.ai.phenomena.ExternalPhenomenaType

data class PolledAttribute(val attributableTag : String, val howPerceived : ExternalPhenomenaType, val attributeValue : AttributeValue<*>)
