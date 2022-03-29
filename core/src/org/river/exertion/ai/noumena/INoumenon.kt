package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.Attributable
import org.river.exertion.ai.attributes.AttributeValue
import org.river.exertion.ai.attributes.IAttributable
import org.river.exertion.ai.attributes.PolledAttribute
import org.river.exertion.ai.phenomena.ExternalPhenomenaType

interface INoumenon {

    fun tag() : String
    fun tags() : MutableList<String>
    fun attributables() : MutableList<Attributable>

    companion object {
        fun mergeOverrideSuperAttributes(superAttributables : MutableList<Attributable>, thisAttributables : MutableList<Attributable>) : MutableList<Attributable> {
            val thisTags = thisAttributables.map { it.attributable.tag }
            superAttributables.forEach { entry -> if (!thisTags.contains(entry.attributable.tag)) thisAttributables.add(entry) }
            return thisAttributables
        }

        fun MutableList<Attributable>.getRandomAttributes() : MutableList<PolledAttribute> {
            val returnList = mutableListOf<PolledAttribute>()

            this.forEach { returnList.add(PolledAttribute(it.attributable.tag, it.attributable.howPerceived, it.attributable.getRandomAttributeValue()) ) }

            return returnList
        }
    }
}