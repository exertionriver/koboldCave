package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.IAttributable

open class HumanoidNoumenon : INoumenon, BeingNoumenon() {

    override val tag = tag()

    //ranges of attributes
    override var attributables: MutableMap<IAttributable<*>, Int> = attributables()

    companion object {
        fun tag() = "humanoid"

        fun tags() = BeingNoumenon.tags() + mutableListOf(tag())

        fun attributables() = INoumenon.mergeOverrideSuperAttributes(BeingNoumenon.attributables(), mutableMapOf(

        ))
    }
}