package org.river.exertion.ai.attributes

import org.river.exertion.ai.phenomena.ExternalPhenomenaType

class GrowlAttributable(override var minValue : String = "", override var maxValue : String = "") : IAttributable<String> {

    override val tag = tag()
    override val howPerceived = howPercevied()
    override var values = values()

    companion object {
        fun tag() = "growl"

        fun howPercevied() = ExternalPhenomenaType.AUDITORY

        fun values() = mutableListOf(
                AttributeValue("kobold", 0, "a gravellish growl")
                , AttributeValue("orc", 1, "a husky growl")
                , AttributeValue("goblin", 2, "a low growl")
        )
    }
}