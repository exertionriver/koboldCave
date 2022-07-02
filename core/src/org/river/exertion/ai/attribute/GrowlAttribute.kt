package org.river.exertion.ai.attribute

import org.river.exertion.ai.noumena.NoneNoumenon
import org.river.exertion.ai.noumena.other.being.humanoid.low_race.KoboldNoumenon
import org.river.exertion.ai.phenomena.ExternalPhenomenaType

object GrowlAttribute : IAttribute<String> {

    override fun type() = AttributeType.GROWL
    override fun howPerceived() = ExternalPhenomenaType.AUDITORY

    override fun attributeValues() = listOf(
            AttributeValue(KoboldNoumenon.type().tag(),"gravellish growl", 0)
            , AttributeValue(KoboldNoumenon.type().tag(), "husky growl", 1)
            , AttributeValue(KoboldNoumenon.type().tag(),"low growl", 2)
    )

    fun growlRange(lambda : Trait<String>.() -> Unit) = Trait(attributeObj = this@GrowlAttribute, noumenonObj = NoneNoumenon).apply(lambda)

    override fun equals(other: Any?): Boolean = this.type() == (other as IAttribute<*>).type()
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}