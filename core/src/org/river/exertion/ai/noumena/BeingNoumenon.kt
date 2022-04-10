package org.river.exertion.ai.noumena

import org.river.exertion.ai.attribute.Characteristic.Companion.mergeOverrideCharacteristics

object BeingNoumenon : INoumenon {

    override fun type() = NoumenonType.BEING
    override fun types() = OtherNoumenon.types().toMutableList().apply { this.add(type()) }.toList()
    override fun characteristics() = OtherNoumenon.characteristics().mergeOverrideCharacteristics(listOf())
}