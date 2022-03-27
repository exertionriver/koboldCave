package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.IAttributable

open class GroupNoumenon : INoumenon {

    override val tag = "group"

    //ranges of attributes
    override var attributables: MutableMap<IAttributable<*>, Int> = mutableMapOf()

}