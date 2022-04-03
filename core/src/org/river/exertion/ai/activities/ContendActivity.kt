package org.river.exertion.ai.activities

import org.river.exertion.ai.internalState.InternalState

interface ContendActivity {

    fun strengthIncreases() : InternalState
    fun strengthDescreases() : InternalState
    fun weaknessIncreases() : InternalState
    fun weaknessDecreases() : InternalState
    fun threatIncreases() : InternalState
    fun threatDescreases() : InternalState
    fun opportunityIncreases() : InternalState
    fun opportunityDecreases() : InternalState

}