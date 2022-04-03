package org.river.exertion.ai.memory

import org.river.exertion.ai.internalState.InternalState
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance

class RegisterExecutive {

    //populated to begin, updated by resolution, other information
    var associativePerceptionList = mutableListOf<PerceivedAttributable>()
    var associativeNoumenaList = mutableListOf<PerceivedNoumenon>()

}