package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.IAttributable

class RedHandNoumenon : INoumenon {

    override fun tag() = "red hand"
    override fun tags() = GroupNoumenon.tags().apply { this.addAll( mutableListOf(BeingNoumenon.tag()) ) }
    override fun attributables() = INoumenon.mergeOverrideSuperAttributes(GroupNoumenon.attributables(), mutableListOf())
}