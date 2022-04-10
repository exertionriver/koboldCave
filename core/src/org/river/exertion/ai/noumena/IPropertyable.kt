package org.river.exertion.ai.noumena

import org.river.exertion.ai.property.Quality

interface IPropertyable {

    fun qualities() : List<Quality<*>>
}