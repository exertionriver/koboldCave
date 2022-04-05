package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.AttributeInstance
import org.river.exertion.ai.attributes.AttributeRange

interface INoumenon {

    fun type() : NoumenonType
    fun types() : List<NoumenonType>
    fun attributeRange() : List<AttributeRange<*>>

}