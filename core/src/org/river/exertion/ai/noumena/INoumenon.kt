package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.AttributeValue
import org.river.exertion.ai.attributes.IAttributable
import org.river.exertion.ai.phenomena.ExternalPhenomenaType

interface INoumenon {

    val tag : String
    var attributables : MutableMap<IAttributable<*>, Int>

    companion object {
        fun mergeOverrideSuperAttributes(superAttributables : MutableMap<IAttributable<*>, Int>, thisAttributables : MutableMap<IAttributable<*>, Int>) : MutableMap<IAttributable<*>, Int> {
            val thisTags = thisAttributables.map { it.key.tag }
            superAttributables.forEach { entry -> if (!thisTags.contains(entry.key.tag)) thisAttributables.put(entry.key, entry.value) }
            return thisAttributables
        }

        fun MutableMap<IAttributable<*>, Int>.getRandomAttributes() : MutableMap<String, Pair<ExternalPhenomenaType, AttributeValue<*>>> =
                this.entries.map { it.key.tag }.associateWith { keyTag -> val attributable = this.entries.find { it.key.tag == keyTag }!! ;
                    Pair(attributable.key.howPerceived, attributable.key.getRandomAttributeValue()) }.toMutableMap()

    }
}