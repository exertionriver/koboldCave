package org.river.exertion.ai.noumena

object NoneNoumenon : INoumenon, IAttributeable, IPropertyable {

    override fun type() = NoumenonType.NONE
    override fun types() = listOf(type())
}