package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.IAttributable

open class OtherNoumenon : INoumenon {

    override val tag = tag()

    //ranges of attributes
    override var attributables: MutableMap<IAttributable<*>, Int> = mutableMapOf()

    companion object {
        fun tag() = "other"
    }
}