package org.river.exertion.ai.attributes

import org.river.exertion.ai.phenomena.ExternalPhenomenaType

object InternalStateAttribute : IAttribute<Float> {

    override fun type() = AttributeType.INTERNAL_STATE
    override fun howPerceived() = ExternalPhenomenaType.WISDOM

    override fun values() = listOf(
            AttributeValue(0.4f, "least hallucinating", 0)
            , AttributeValue(0.5f, "hallucinating", 1)
            , AttributeValue(0.6f, "most hallucinating", 2)
    )

    fun internalStateRange(lambda : AttributeRange<Float>.() -> Unit) = AttributeRange(attributeObj = this@InternalStateAttribute.javaClass).apply(lambda)

    override fun equals(other: Any?): Boolean = this.type() == (other as IAttribute<*>).type()
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}