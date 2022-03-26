package org.river.exertion.ai.attributes

class IntelligenceAttribute(var value : Int) {

    fun getDescription() = IntelligenceAttributable(value, value).getDescriptions().first()
}