package org.river.exertion.ai.attribute

import org.river.exertion.ai.noumena.INoumenon
import org.river.exertion.ai.noumena.NoneNoumenon

data class Characteristic<T:Any>(var attributeObj: Class<IAttribute<T>> = (NoneAttribute as IAttribute<T>).javaClass, var noumenonObj: Class<INoumenon> = (NoneNoumenon as INoumenon).javaClass, var characteristicValue: AttributeValue<out Any> = AttributeValue(), var noumenonOrder: Int = 0) {

    fun attribute() : IAttribute<T> = attributeObj.kotlin.objectInstance!!
    fun noumenon() : INoumenon = noumenonObj.kotlin.objectInstance!!
}