package org.river.exertion.ai.attributes

class KoboldAttributable {

    //ranges of attributes
    var attributables = mutableMapOf<IAttributable<*>, Int>(
        InternalStateAttributable(0.4f, 0.6f) to 3,
        IntelligenceAttributable(6, 8) to 8
    )

    fun getRandomAttributes() : MutableMap<String, AttributeValue<*>> =
        attributables.entries.map { it.key.tag }.associateWith { keyTag -> attributables.entries.find { it.key.tag == keyTag }!!.key.getRandomAttributeValue() }.toMutableMap()
}