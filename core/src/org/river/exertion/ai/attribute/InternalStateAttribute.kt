package org.river.exertion.ai.attribute

import org.river.exertion.ai.phenomena.ExternalPhenomenaType

object InternalStateAttribute : IAttribute<Float> {

    override fun type() = AttributeType.INTERNAL_STATE
    override fun howPerceived() = ExternalPhenomenaType.WISDOM

    override fun attributeValues() = listOf(
            AttributeValue(0.4f, "least hallucinating", 0)
            , AttributeValue(0.5f, "hallucinating", 1)
            , AttributeValue(0.6f, "most hallucinating", 2)
    )

    fun internalStateRange(lambda : Trait<Float>.() -> Unit) = Trait(attributeObj = this@InternalStateAttribute.javaClass).apply(lambda)

    override fun equals(other: Any?): Boolean = this.type() == (other as IAttribute<*>).type()
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}