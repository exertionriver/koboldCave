package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.AttributeInstance
import org.river.exertion.ai.attributes.AttributeRange

interface INoumenon {

    fun tag() : String
    fun tags() : List<String>
    fun attributeRange() : List<AttributeRange<*>>

}