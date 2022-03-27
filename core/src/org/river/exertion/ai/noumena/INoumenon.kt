package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.AttributeValue
import org.river.exertion.ai.attributes.IAttributable

interface INoumenon {

    val tag : String
    var attributables : MutableMap<IAttributable<*>, Int>

    fun getRandomAttributes() : MutableMap<String, AttributeValue<*>> =
            attributables.entries.map { it.key.tag }.associateWith { keyTag -> attributables.entries.find { it.key.tag == keyTag }!!.key.getRandomAttributeValue() }.toMutableMap()

    companion object {
        fun mergeOverrideSuper(superAttributables : MutableMap<IAttributable<*>, Int>, thisAttributables : MutableMap<IAttributable<*>, Int>) : MutableMap<IAttributable<*>, Int> {
            val thisTags = thisAttributables.map { it.key.tag }
            superAttributables.forEach { entry -> if (!thisTags.contains(entry.key.tag)) thisAttributables.put(entry.key, entry.value) }
            return thisAttributables
        }
    }
}