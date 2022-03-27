package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.IAttributable

open class GroupNoumenon : INoumenon {

    override val tag = tag()

    //ranges of attributes
    override var attributables: MutableMap<IAttributable<*>, Int> = attributables()

    companion object {
        fun tag() = "group"

        fun tags() = mutableListOf(tag())

        fun attributables() : MutableMap<IAttributable<*>, Int> = mutableMapOf()
    }
}