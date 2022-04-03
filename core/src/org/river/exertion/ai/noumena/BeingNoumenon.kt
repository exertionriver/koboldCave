package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.IAttributable

object BeingNoumenon : INoumenon {

    override fun tag() = "being"
    override fun tags() = OtherNoumenon.tags().apply { this.add(tag()) }
    override fun attributables() = INoumenon.mergeOverrideSuperAttributes(OtherNoumenon.attributables(), mutableListOf())
}