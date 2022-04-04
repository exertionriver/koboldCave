package org.river.exertion.ai.attributes

import java.lang.reflect.Type

data class AttributeInstance<T:Any>(private val attributeType : Type, val attributeValue : AttributeValue<T>, val noumenonOrder : Int = 0) {

    val attribute: IAttribute<T> = attributeType as IAttribute<T>
}
