package org.river.exertion.ai.components

import com.badlogic.ashley.core.Component
import org.river.exertion.ai.attributes.AttributeValue
import org.river.exertion.ai.noumena.INoumenon
import org.river.exertion.ai.phenomena.ExternalPhenomenaType

interface IAttributesComponent : Component {

    val noumenon : INoumenon
    var attributes : MutableMap<String, Pair<ExternalPhenomenaType, AttributeValue<*>>>
}