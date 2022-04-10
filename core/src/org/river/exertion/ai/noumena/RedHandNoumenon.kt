package org.river.exertion.ai.noumena

import org.river.exertion.ai.attribute.Characteristic.Companion.mergeOverrideCharacteristics
import org.river.exertion.ai.property.Quality

class RedHandNoumenon : INoumenon {

    override fun type() = NoumenonType.RED_HAND
    override fun types() = GroupNoumenon.types().toMutableList().apply { this.add( type() ) }.toList()
    override fun characteristics() = GroupNoumenon.characteristics().mergeOverrideCharacteristics(listOf())
}