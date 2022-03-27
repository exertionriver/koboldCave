package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.IAttributable

open class BeingNoumenon : INoumenon, OtherNoumenon() {

    override val tag = tag()

    //ranges of attributes
    override var attributables: MutableMap<IAttributable<*>, Int> = attributables()

    companion object {
        fun tag() = "being"

        fun tags() = OtherNoumenon.tags() + mutableListOf(tag())

        fun attributables() = INoumenon.mergeOverrideSuperAttributes(OtherNoumenon.attributables(), mutableMapOf(

        ))
    }
}