package org.river.exertion.ai.noumena

object NoneNoumenon : INoumenon {

    override fun type() = NoumenonType.NONE
    override fun types() = listOf(type())
}