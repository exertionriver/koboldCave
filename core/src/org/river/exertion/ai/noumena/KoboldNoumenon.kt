package org.river.exertion.ai.noumena

import org.river.exertion.ai.attribute.Characteristic.Companion.mergeOverrideCharacteristics
import org.river.exertion.ai.attribute.GrowlAttribute.growlRange
import org.river.exertion.ai.attribute.IntelligenceAttribute.intelligenceRange
import org.river.exertion.ai.attribute.InternalStateAttribute.internalStateRange
import java.util.*

object KoboldNoumenon : InstantiatableNoumenon {

    override fun type() = NoumenonType.KOBOLD
    override fun types() = LowRaceNoumenon.types().toMutableList().apply { this.add(type()) }.toList()
    override fun characteristics() = LowRaceNoumenon.characteristics().mergeOverrideCharacteristics( listOf(
        growlRange { noumenonObj = this@KoboldNoumenon.javaClass; noumenonOrder = 2; minValue = type().tag(); maxValue = type().tag() },
        internalStateRange { noumenonObj = this@KoboldNoumenon.javaClass; noumenonOrder = 3; minValue = 0.5f; maxValue = 0.6f },
        intelligenceRange { noumenonObj = this@KoboldNoumenon.javaClass; noumenonOrder = 8; minValue = 7; maxValue = 8 }
    ))

    fun kobold(lambda : NoumenonInstance.() -> Unit) = NoumenonInstance(sourceNoumenonType = KoboldNoumenon.javaClass, instanceName = "razza" + Random().nextInt()).apply(lambda)

}