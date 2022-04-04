package org.river.exertion.ai.internalState

import org.river.exertion.ai.activities.ContendActivity

object FearState : IInternalState, ContendActivity {

    override val tag = "fear"

    override fun strengthIncreases(): IInternalState {
        TODO("Not yet implemented")
    }

    override fun strengthDescreases(): IInternalState {
        TODO("Not yet implemented")
    }

    override fun weaknessIncreases(): IInternalState {
        TODO("Not yet implemented")
    }

    override fun weaknessDecreases(): IInternalState {
        TODO("Not yet implemented")
    }

    override fun threatIncreases(): IInternalState {
        TODO("Not yet implemented")
    }

    override fun threatDescreases(): IInternalState {
        TODO("Not yet implemented")
    }

    override fun opportunityIncreases(): IInternalState {
        TODO("Not yet implemented")
    }

    override fun opportunityDecreases(): IInternalState {
        TODO("Not yet implemented")
    }

    fun fearState(lambda: () -> Float) : InternalStateInstance = InternalStateInstance(internalState = FearState, magnitude = lambda.invoke())
}