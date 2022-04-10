package org.river.exertion.ai.property

import org.river.exertion.ai.phenomena.ExternalPhenomenaType

object NoneProperty : IProperty<String> {

    override fun type() = PropertyType.NONE
    override fun howPerceived() = ExternalPhenomenaType.NONE

    override fun propertyValues() = listOf<PropertyValue<String>>()

    override fun equals(other: Any?): Boolean = this.type() == (other as IProperty<*>).type()
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}