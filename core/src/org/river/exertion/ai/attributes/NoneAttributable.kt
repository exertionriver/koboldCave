package org.river.exertion.ai.attributes

import org.river.exertion.ai.phenomena.ExternalPhenomenaType

class NoneAttributable(override var minValue : String = "", override var maxValue : String = "") : IAttributable<String> {

    override val tag = tag()
    override val howPerceived = howPercevied()
    override var values = values()

    companion object {
        fun tag() = "none"

        fun howPercevied() = ExternalPhenomenaType.NONE

        fun values() = mutableListOf<AttributeValue<String>>()
    }
}