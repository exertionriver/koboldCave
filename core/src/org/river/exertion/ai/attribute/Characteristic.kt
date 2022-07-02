package org.river.exertion.ai.attribute

import org.river.exertion.ai.noumena.core.INoumenon

data class Characteristic<T:Any>(var attributeObj: IAttribute<T>, var noumenonObj: INoumenon, var characteristicValue: AttributeValue<out Any> = AttributeValue(), var noumenonOrder: Int = 0) {

//    fun attribute() : IAttribute<T> = attributeObj.kotlin.objectInstance!!
//    fun noumenon() : INoumenon = noumenonObj.kotlin.objectInstance!!
}