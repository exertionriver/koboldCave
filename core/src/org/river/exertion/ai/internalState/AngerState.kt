package org.river.exertion.ai.internalState

import org.river.exertion.ai.activities.ContendActivity

object AngerState : IInternalState, ContendActivity {

    override val tag = "anger"

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

    fun angerState(lambda: () -> Float) : InternalStateInstance = InternalStateInstance(internalState = AngerState, magnitude = lambda.invoke())

}