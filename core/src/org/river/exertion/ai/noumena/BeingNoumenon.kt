package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.IAttributable

open class BeingNoumenon : INoumenon, OtherNoumenon() {

    //ranges of attributes
    override var attributables: MutableMap<IAttributable<*>, Int> =
        super.attributables.also { it.putAll(mutableMapOf(

        ) ) }
}