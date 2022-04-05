package org.river.exertion.ai.attributes

import org.river.exertion.ai.noumena.KoboldNoumenon
import org.river.exertion.ai.noumena.NoumenonType
import org.river.exertion.ai.phenomena.ExternalPhenomenaType

object GrowlAttribute : IAttribute<String> {

    override fun type() = AttributeType.GROWL
    override fun howPerceived() = ExternalPhenomenaType.AUDITORY

    override fun values() = listOf(
            AttributeValue(KoboldNoumenon.type().tag(),"a gravellish growl", 0)
            , AttributeValue(KoboldNoumenon.type().tag(), "a husky growl", 1)
            , AttributeValue(KoboldNoumenon.type().tag(),"a low growl", 2)
    )

    fun growlRange(lambda : AttributeRange<String>.() -> Unit) = AttributeRange(attributeObj = this@GrowlAttribute.javaClass).apply(lambda)

    override fun equals(other: Any?): Boolean = this.type() == (other as IAttribute<*>).type()
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}