package org.river.exertion.ai.memory

import org.river.exertion.ai.internalState.InternalFacetInstancesState

class InternalMemory {

    val registerExecutive = RegisterExecutive()
    val encounterMemory = EncounterMemory()
    val longtermMemory = LongtermMemory()

    val internalState = InternalFacetInstancesState()
}