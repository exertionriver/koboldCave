package org.river.exertion.koboldQueue.state

class RegisterState(override val state: String, override val ordering: Int) : State(state, ordering) {

    companion object {
        val WatchState = RegisterState("watch", 0)
    }
}
