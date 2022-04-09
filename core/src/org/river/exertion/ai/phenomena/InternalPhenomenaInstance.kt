package org.river.exertion.ai.phenomena

import org.river.exertion.ai.internalFacet.NoneFacet.noneFacet

class InternalPhenomenaInstance {

    var arisenFacet = noneFacet {}
    fun fade() = arisenFacet.magnitude * 10 //% per time unit; first pass

    fun impression() : InternalPhenomenaImpression {
        return InternalPhenomenaImpression().apply {
            arisenFacet = this@InternalPhenomenaInstance.arisenFacet
            countdown = this@InternalPhenomenaInstance.fade()
        }
    }
}


