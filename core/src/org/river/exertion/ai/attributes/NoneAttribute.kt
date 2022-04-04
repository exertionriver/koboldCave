package org.river.exertion.ai.attributes

import org.river.exertion.ai.phenomena.ExternalPhenomenaType

object NoneAttribute : IAttribute<String> {

    override fun tag() = "none"
    override fun howPerceived() = ExternalPhenomenaType.NONE

    override fun values() = listOf<AttributeValue<String>>()

    override fun equals(other: Any?): Boolean = this.tag() == (other as IAttribute<*>).tag()
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}