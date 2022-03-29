package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.Attributable

object GroupNoumenon : INoumenon {

    override fun tag() = "group"
    override fun tags() = mutableListOf(tag())
    override fun attributables() : MutableList<Attributable> = mutableListOf()
}