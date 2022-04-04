package org.river.exertion.ai.attributes

import org.river.exertion.ai.phenomena.ExternalPhenomenaType

object GrowlAttribute : IAttribute<String> {

    override fun tag() = "growl"
    override fun howPerceived() = ExternalPhenomenaType.AUDITORY

    override fun values() = listOf(
            AttributeValue("kobold","a gravellish growl", 0)
            , AttributeValue("orc", "a husky growl", 1)
            , AttributeValue("goblin","a low growl", 2)
    )

    override fun equals(other: Any?): Boolean = this.tag() == (other as IAttribute<*>).tag()
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}