package org.river.exertion.ai.memory

import org.river.exertion.ai.Knowable
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance

class AssociativeMemory {

    var knowable = Knowable()
    var internalPhenomenaInstance = InternalPhenomenaInstance()
    var becauseOf : AssociativeMemory? = null

}