package org.river.exertion.ai.phenomena

import org.river.exertion.ai.internalState.NoneFacet.noneFacet

class InternalPhenomenaInstance {

    var arising = noneFacet {}
    var loss = 0f



    fun impression() : InternalPhenomenaImpression {
        return InternalPhenomenaImpression().apply {
            arising = arising

            countdown = 10f
        }
    }
}


