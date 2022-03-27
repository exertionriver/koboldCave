package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.IAttributable
import org.river.exertion.ai.attributes.IntelligenceAttributable
import org.river.exertion.ai.attributes.InternalStateAttributable

open class LowRaceNoumenon : INoumenon, HumanoidNoumenon() {

    override val tag = tag()

    //ranges of attributes
    override var attributables: MutableMap<IAttributable<*>, Int> = attributables()

    companion object {
        fun tag() = "low race"

        fun attributables() = INoumenon.mergeOverrideSuper(HumanoidNoumenon.attributables(), mutableMapOf(
            InternalStateAttributable(0.4f, 0.6f) to 3,
            IntelligenceAttributable(6, 8) to 8
        ))
    }
}