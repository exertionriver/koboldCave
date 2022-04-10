package org.river.exertion.ai.attribute

import org.river.exertion.ai.noumena.KoboldNoumenon
import org.river.exertion.ai.phenomena.ExternalPhenomenaType

object GrowlAttribute : IAttribute<String> {

    override fun type() = AttributeType.GROWL
    override fun howPerceived() = ExternalPhenomenaType.AUDITORY

    override fun attributeValues() = listOf(
            AttributeValue(KoboldNoumenon.type().tag(),"gravellish growl", 0)
            , AttributeValue(KoboldNoumenon.type().tag(), "husky growl", 1)
            , AttributeValue(KoboldNoumenon.type().tag(),"low growl", 2)
    )

    fun growlRange(lambda : Characteristic<String>.() -> Unit) = Characteristic(attributeObj = this@GrowlAttribute.javaClass).apply(lambda)

    override fun equals(other: Any?): Boolean = this.type() == (other as IAttribute<*>).type()
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}