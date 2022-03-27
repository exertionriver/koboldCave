package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.IAttributable

open class HumanoidNoumenon : INoumenon, BeingNoumenon() {

    override val tag = "humanoid"

    //ranges of attributes
    override var attributables: MutableMap<IAttributable<*>, Int> =
        super.attributables.also { it.putAll(mutableMapOf(

        ) ) }
}