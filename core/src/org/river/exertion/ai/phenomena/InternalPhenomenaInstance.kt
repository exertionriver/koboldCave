package org.river.exertion.ai.phenomena

import org.river.exertion.ai.manifest.InternalState
import org.river.exertion.ai.manifest.InternalState.Companion.div
import org.river.exertion.ai.manifest.InternalState.Companion.magnitude
import org.river.exertion.ai.manifest.InternalState.Companion.minus
import org.river.exertion.ai.manifest.InternalState.Companion.plus
import org.river.exertion.ai.manifest.InternalStateBiases

class InternalPhenomenaInstance {

    var origin = InternalStateBiases.none()
    var arising = InternalStateBiases.none()
    fun change() = arising - origin

    var loss = 0f

    fun magnitude() = change().magnitude()

    fun impression() : InternalPhenomenaImpression {
        return InternalPhenomenaImpression().apply {
            origin = origin
            arising = arising
            change = change

            countdown = 10f
        }
    }

    companion object {
        fun List<InternalPhenomenaInstance>.opinion() : InternalState {
            var resultState = InternalStateBiases.none()

            this.forEach {
                resultState += it.change()
            }

            if (this.isNotEmpty()) {
                resultState /= this.size
            }

            return resultState
        }
    }
}


