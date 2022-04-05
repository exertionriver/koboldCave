package org.river.exertion.ai.attributes

import org.river.exertion.ai.phenomena.ExternalPhenomenaType

object IntelligenceAttribute : IAttribute<Int> {

    override fun type() = AttributeType.INTELLIGENCE
    override fun howPerceived() = ExternalPhenomenaType.WISDOM

    override fun values() = listOf(
            AttributeValue(6, "momma's angel", 0)
            , AttributeValue(7, "schoolyard bully", 1)
            , AttributeValue(8, "smiley Tim", 2)
    )

    fun intelligenceRange(lambda : AttributeRange<Int>.() -> Unit) = AttributeRange(attributeObj = this@IntelligenceAttribute.javaClass).apply(lambda)

    override fun equals(other: Any?): Boolean = this.type() == (other as IAttribute<*>).type()
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}