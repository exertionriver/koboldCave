package org.river.exertion.ai.attributes

import org.river.exertion.ai.phenomena.ExternalPhenomenaType

class IntelligenceAttributable(override var minValue : Int, override var maxValue : Int) : IAttributable<Int> {

    override val tag = tag()
    override val howPerceived = howPercevied()
    override var values = values()

    companion object {
        fun tag() = "intelligence"

        fun howPercevied() = ExternalPhenomenaType.WISDOM

        fun values() = mutableListOf(
                AttributeValue(6, 0, "momma's angel")
                , AttributeValue(7, 1, "schoolyard bully")
                , AttributeValue(8, 2, "smiley Tim")
        )
    }
}