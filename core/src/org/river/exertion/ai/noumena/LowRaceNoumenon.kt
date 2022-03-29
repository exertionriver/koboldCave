package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.Attributable
import org.river.exertion.ai.attributes.IAttributable
import org.river.exertion.ai.attributes.IntelligenceAttributable
import org.river.exertion.ai.attributes.InternalStateAttributable

object LowRaceNoumenon : INoumenon {

    override fun tag() = "low race"
    override fun tags() = HumanoidNoumenon.tags().apply { this.addAll(mutableListOf(HumanoidNoumenon.tag())) }
    override fun attributables() = INoumenon.mergeOverrideSuperAttributes(HumanoidNoumenon.attributables(), mutableListOf(
        Attributable(InternalStateAttributable(0.4f, 0.6f), 3),
        Attributable(IntelligenceAttributable(6, 8), 8)
    ))
}