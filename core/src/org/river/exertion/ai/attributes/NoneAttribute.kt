package org.river.exertion.ai.attributes

import org.river.exertion.ai.phenomena.ExternalPhenomenaType

object NoneAttribute : IAttribute<String> {

    override fun type() = AttributeType.NONE
    override fun howPerceived() = ExternalPhenomenaType.NONE

    override fun values() = listOf<AttributeValue<String>>()

    override fun equals(other: Any?): Boolean = this.type() == (other as IAttribute<*>).type()
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}