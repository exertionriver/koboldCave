package org.river.exertion.ai.noumena

import org.river.exertion.ai.attribute.Characteristic

interface IAttributeable {

    fun characteristics() : List<Characteristic<*>>
}