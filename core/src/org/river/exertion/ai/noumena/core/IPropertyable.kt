package org.river.exertion.ai.noumena.core

import org.river.exertion.ai.property.Quality

interface IPropertyable {

    fun qualities() : List<Quality<*>>
}