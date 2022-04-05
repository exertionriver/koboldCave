package org.river.exertion.ai.attributes

import org.river.exertion.ai.noumena.INoumenon
import org.river.exertion.ai.noumena.NoneNoumenon

data class AttributeInstance<T:Any>(var attributeObj: Class<IAttribute<T>> = (NoneAttribute as IAttribute<T>).javaClass, var noumenonObj: Class<INoumenon> = (NoneNoumenon as INoumenon).javaClass, var attributeValue: AttributeValue<out Any> = AttributeValue(), var noumenonOrder: Int = 0) {

    fun attribute() : IAttribute<T> = attributeObj.kotlin.objectInstance!!
    fun noumenon() : INoumenon = noumenonObj.kotlin.objectInstance!!
}