package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.*

object KoboldNoumenon : INoumenon {

    override fun tag() = "kobold"
    override fun tags() = LowRaceNoumenon.tags().apply { this.addAll(mutableListOf(LowRaceNoumenon.tag())) }
    override fun attributables() = INoumenon.mergeOverrideSuperAttributes(LowRaceNoumenon.attributables(), mutableListOf(
        Attributable(GrowlAttributable(tag(), tag()), 2),
        Attributable(InternalStateAttributable(0.5f, 0.6f), 3),
        Attributable(IntelligenceAttributable(7, 8), 8)
    ))
}