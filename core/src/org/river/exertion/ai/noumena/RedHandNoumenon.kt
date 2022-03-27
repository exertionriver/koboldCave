package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.IAttributable

class RedHandNoumenon : INoumenon, GroupNoumenon() {

    override val tag = tag()

    //ranges of attributes
    override var attributables: MutableMap<IAttributable<*>, Int> = attributables()

    companion object {
        fun tag() = "red hand"

        fun attributables() = INoumenon.mergeOverrideSuper(GroupNoumenon.attributables(), mutableMapOf(

        ))
    }
}