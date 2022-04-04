package org.river.exertion.ai.attributes

data class AttributeInstance<T:Any>(private val attributeType: Class<IAttribute<T>>, val attributeValue: AttributeValue<out Any>, val noumenonOrder: Int = 0) {

    val attribute: IAttribute<T> = attributeType.kotlin.objectInstance!!
}
