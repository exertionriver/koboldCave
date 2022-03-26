package org.river.exertion.ai.attributes

class KoboldAttributable {

    //ranges of attributes
    var attributables = mutableMapOf<IAttributable<*>, Int>(
        InternalStateAttributable(0.4f, 0.6f) to 3,
        IntelligenceAttributable(6, 8) to 8
    )
}