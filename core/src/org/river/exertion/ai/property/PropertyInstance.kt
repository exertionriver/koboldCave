package org.river.exertion.ai.property

import org.river.exertion.ai.noumena.core.INoumenon
import org.river.exertion.ai.noumena.NoneNoumenon

data class PropertyInstance<T:Any>(var propertyObj: Class<IProperty<T>> = (NoneProperty as IProperty<T>).javaClass, var noumenonObj: Class<INoumenon> = (NoneNoumenon as INoumenon).javaClass, var propertyValue: PropertyValue<out Any> = PropertyValue(), var noumenonOrder: Int = 0) {

    fun property() : IProperty<T> = propertyObj.kotlin.objectInstance!!
    fun noumenon() : INoumenon = noumenonObj.kotlin.objectInstance!!
}