package org.river.exertion.ai.memory

import org.river.exertion.ai.manifest.InternalManifest
import org.river.exertion.ai.perception.PerceivedNoumenon
import org.river.exertion.ai.perception.PerceivedExternalPhenomena

class RegisterExecutive : IMemory {

    override var noumenaRegister = mutableListOf<PerceivedNoumenon>()

    //scans manifest for phenomena, adds max-intelligence attibutes / noumena to register
    fun think(characterManifest : InternalManifest, intelligenceValue : Int ) {
        noumenaRegister.clear()

        val presentPhenomenaList = mutableListOf<PerceivedExternalPhenomena>()

        (0 until intelligenceValue).forEach { idx ->
            presentPhenomenaList.add(characterManifest.pollRandomExternalPhenomena(presentPhenomenaList))
        }

//        presentPhenomenaList.forEach { presentAttributeRegister.add( it.sender.noumenon.pollRandomAttribute(it.externalPhenomenaImpression.type) ) }

    }

    //forms max-wisdom opinions based on registers
    fun reflect() {

    }


}