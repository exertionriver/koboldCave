package org.river.exertion.ai.internalState

object NoneState : InternalState {

    override val tag = "none"

    fun noneState(lambda: () -> Unit) : InternalStateInstance = InternalStateInstance(internalState = FearState, magnitude = 0f)
}