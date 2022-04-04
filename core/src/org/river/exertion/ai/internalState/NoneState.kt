package org.river.exertion.ai.internalState

object NoneState : IInternalState {

    override val tag = "none"

    fun noneState(lambda: () -> Unit) : InternalStateInstance = InternalStateInstance(internalState = FearState, magnitude = 0f)
}