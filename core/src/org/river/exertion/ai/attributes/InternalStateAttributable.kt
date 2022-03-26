package org.river.exertion.ai.attributes

class InternalStateAttributable(override var minValue : Float, override var maxValue : Float) : IAttributable<Float> {

    override var values = mutableListOf(
        AttributeValue(0.4f, 0, "least hallucinating")
        , AttributeValue(0.5f, 1, "hallucinating")
        , AttributeValue(0.6f, 2, "most hallucinating")
    )
}