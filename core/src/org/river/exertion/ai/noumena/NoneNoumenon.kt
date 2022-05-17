package org.river.exertion.ai.noumena

import org.river.exertion.ai.noumena.core.INoumenon
import org.river.exertion.ai.noumena.core.NoumenonType

object NoneNoumenon : INoumenon {

    override fun type() = NoumenonType.NONE
    override fun types() = listOf(type())
}