package org.river.exertion.ai.internalState

import org.river.exertion.ai.activities.ContendActivity

object FearState : InternalState, ContendActivity {

    override val tag = "fear"

    override fun strengthIncreases(): InternalState {
        TODO("Not yet implemented")
    }

    override fun strengthDescreases(): InternalState {
        TODO("Not yet implemented")
    }

    override fun weaknessIncreases(): InternalState {
        TODO("Not yet implemented")
    }

    override fun weaknessDecreases(): InternalState {
        TODO("Not yet implemented")
    }

    override fun threatIncreases(): InternalState {
        TODO("Not yet implemented")
    }

    override fun threatDescreases(): InternalState {
        TODO("Not yet implemented")
    }

    override fun opportunityIncreases(): InternalState {
        TODO("Not yet implemented")
    }

    override fun opportunityDecreases(): InternalState {
        TODO("Not yet implemented")
    }

    fun fearState(lambda: () -> Float) : InternalStateInstance = InternalStateInstance(internalState = FearState, magnitude = lambda.invoke())
}