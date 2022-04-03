package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.IAttributable

object HumanoidNoumenon : INoumenon {

    override fun tag() = "humanoid"
    override fun tags() = BeingNoumenon.tags().apply { this.add(tag()) }
    override fun attributables() = INoumenon.mergeOverrideSuperAttributes(BeingNoumenon.attributables(), mutableListOf())
}