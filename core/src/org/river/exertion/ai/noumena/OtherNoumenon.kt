package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.Attributable

object OtherNoumenon : INoumenon {

    override fun tag() = "other"
    override fun tags() = mutableListOf(tag())
    override fun attributables() : MutableList<Attributable> = mutableListOf()
}