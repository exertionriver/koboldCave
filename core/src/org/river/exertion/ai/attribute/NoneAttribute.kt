package org.river.exertion.ai.attribute

import org.river.exertion.ai.phenomena.ExternalPhenomenaType

object NoneAttribute : IAttribute<String> {

    override fun type() = AttributeType.NONE
    override fun howPerceived() = ExternalPhenomenaType.NONE

    override fun attributeValues() = listOf<AttributeValue<String>>()

    override fun equals(other: Any?): Boolean = this.type() == (other as IAttribute<*>).type()
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}