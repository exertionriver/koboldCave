package org.river.exertion.ai.noumena.group

import org.river.exertion.ai.attribute.Trait.Companion.mergeOverrideTraits
import org.river.exertion.ai.noumena.GroupNoumenon
import org.river.exertion.ai.noumena.core.IAttributeable
import org.river.exertion.ai.noumena.core.INoumenon
import org.river.exertion.ai.noumena.core.NoumenonType

class RedHandNoumenon : INoumenon, IAttributeable {

    override fun type() = NoumenonType.RED_HAND
    override fun types() = GroupNoumenon.types().toMutableList().apply { this.add( type() ) }.toList()
    override fun traits() = GroupNoumenon.traits().mergeOverrideTraits(listOf())
}