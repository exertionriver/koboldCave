package org.river.exertion.ai.attributes

import org.river.exertion.ai.phenomena.ExternalPhenomenaType

class InternalStateAttributable(override var minValue : Float, override var maxValue : Float) : IAttributable<Float> {

    override val tag = tag()

    override val howPerceived = ExternalPhenomenaType.WISDOM

    override var values = mutableListOf(
        AttributeValue(0.4f, 0, "least hallucinating")
        , AttributeValue(0.5f, 1, "hallucinating")
        , AttributeValue(0.6f, 2, "most hallucinating")
    )

    companion object {
        fun tag() = "internal state"
    }
}