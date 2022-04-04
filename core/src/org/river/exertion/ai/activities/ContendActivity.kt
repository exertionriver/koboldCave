package org.river.exertion.ai.activities

import org.river.exertion.ai.internalState.IInternalState

interface ContendActivity {

    fun strengthIncreases() : IInternalState
    fun strengthDescreases() : IInternalState
    fun weaknessIncreases() : IInternalState
    fun weaknessDecreases() : IInternalState
    fun threatIncreases() : IInternalState
    fun threatDescreases() : IInternalState
    fun opportunityIncreases() : IInternalState
    fun opportunityDecreases() : IInternalState

}