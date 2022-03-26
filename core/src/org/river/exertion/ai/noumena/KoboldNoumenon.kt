package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.IAttributable
import org.river.exertion.ai.attributes.IntelligenceAttributable
import org.river.exertion.ai.attributes.InternalStateAttributable

class KoboldNoumenon : INoumenon, LowRaceNoumenon() {

    //ranges of attributes
    override var attributables: MutableMap<IAttributable<*>, Int> = mergeOverrideSuper(
        mutableMapOf(
            InternalStateAttributable(0.5f, 0.6f) to 3,
            IntelligenceAttributable(7, 8) to 8
        ), super.attributables)
}