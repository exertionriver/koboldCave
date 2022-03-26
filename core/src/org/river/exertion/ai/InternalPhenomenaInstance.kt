package org.river.exertion.ai

class InternalPhenomenaInstance {

    var type = InternalPhenomenaType.NONE
    var magnitude = 0f

    var loss = 0f

    fun impression() : InternalPhenomenaImpression {
        return InternalPhenomenaImpression().apply {
            type = this@InternalPhenomenaInstance.type
            perceivedMagnitude = this@InternalPhenomenaInstance.magnitude

            countdown = 10f
        }
    }
}