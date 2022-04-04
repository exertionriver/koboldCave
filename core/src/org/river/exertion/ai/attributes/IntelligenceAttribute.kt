package org.river.exertion.ai.attributes

import org.river.exertion.ai.phenomena.ExternalPhenomenaType

object IntelligenceAttribute : IAttribute<Int> {

    override fun tag() = "intelligence"
    override fun howPerceived() = ExternalPhenomenaType.WISDOM

    override fun values() = listOf(
            AttributeValue(6, "momma's angel", 0)
            , AttributeValue(7, "schoolyard bully", 1)
            , AttributeValue(8, "smiley Tim", 2)
    )

    override fun equals(other: Any?): Boolean = this.tag() == (other as IAttribute<*>).tag()
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}