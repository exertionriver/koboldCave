package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.IAttributable

class RedHandNoumenon : INoumenon, GroupNoumenon() {

    override val tag = "red hand"

    //ranges of attributes
    override var attributables: MutableMap<IAttributable<*>, Int> =
        super.attributables.also { it.putAll(mutableMapOf(

        ) ) }
}