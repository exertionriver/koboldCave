package org.river.exertion.ai.noumena

import org.river.exertion.ai.attribute.Characteristic.Companion.mergeOverrideCharacteristics

object HumanoidNoumenon : INoumenon {

    override fun type() = NoumenonType.HUMANOID
    override fun types() = BeingNoumenon.types().toMutableList().apply { this.add(type()) }.toList()
    override fun characteristics() = BeingNoumenon.characteristics().mergeOverrideCharacteristics(listOf())
}