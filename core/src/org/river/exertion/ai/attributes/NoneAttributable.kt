package org.river.exertion.ai.attributes

import org.river.exertion.ai.phenomena.ExternalPhenomenaType

class NoneAttributable(override var minValue : String = "", override var maxValue : String = "") : IAttributable<String> {

    override val tag = tag()

    override val howPerceived = ExternalPhenomenaType.NONE

    override var values = mutableListOf<AttributeValue<String>>()

    companion object {
        fun tag() = "none"
    }
}