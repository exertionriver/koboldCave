package org.river.exertion.ai.phenomena

import org.river.exertion.ai.internalState.InternalStateInstance

class InternalPhenomenaInstance {

    var arising = InternalStateInstance.none()
    var loss = 0f

    fun impression() : InternalPhenomenaImpression {
        return InternalPhenomenaImpression().apply {
            arising = arising

            countdown = 10f
        }
    }
}


