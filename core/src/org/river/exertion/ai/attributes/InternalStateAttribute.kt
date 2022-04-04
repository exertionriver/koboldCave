package org.river.exertion.ai.attributes

import org.river.exertion.ai.phenomena.ExternalPhenomenaType

class InternalStateAttribute : IAttribute<Float> {

    override fun tag() = "internal state"
    override fun howPerceived() = ExternalPhenomenaType.WISDOM

    override fun values() = listOf(
            AttributeValue(0.4f, "least hallucinating", 0)
            , AttributeValue(0.5f, "hallucinating", 1)
            , AttributeValue(0.6f, "most hallucinating", 2)
    )

    override fun equals(other: Any?): Boolean = this.tag() == (other as IAttribute<*>).tag()
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}