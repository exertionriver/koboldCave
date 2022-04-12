package org.river.exertion.ai.noumena

import org.river.exertion.ai.attribute.Trait.Companion.mergeOverrideTraits

class RedHandNoumenon : INoumenon, IAttributeable {

    override fun type() = NoumenonType.RED_HAND
    override fun types() = GroupNoumenon.types().toMutableList().apply { this.add( type() ) }.toList()
    override fun traits() = GroupNoumenon.traits().mergeOverrideTraits(listOf())
}