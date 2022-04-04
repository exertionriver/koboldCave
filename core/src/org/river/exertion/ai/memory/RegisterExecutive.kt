package org.river.exertion.ai.memory

import org.river.exertion.ai.attributes.AttributeInstance
import org.river.exertion.ai.attributes.AttributeValue
import org.river.exertion.ai.attributes.IntelligenceAttribute
import org.river.exertion.ai.manifest.CharacterManifest
import org.river.exertion.ai.manifest.PerceivedPhenomena

class RegisterExecutive {

    var presentAttributeRegister = mutableListOf<PerceivedAttributable>()
    var presentNoumenaRegister = mutableListOf<PerceivedNoumenon>()

    //scans manifest for phenomena, adds max-intelligence attibutes / noumena to register
    fun think(characterManifest : CharacterManifest, intelligenceValue : Int ) {
        presentAttributeRegister.clear()
        presentNoumenaRegister.clear()

        val presentPhenomenaList = mutableListOf<PerceivedPhenomena>()

        (0 until intelligenceValue).forEach { idx ->
            presentPhenomenaList.add(characterManifest.pollRandomExternalPhenomena(presentPhenomenaList))
        }

//        presentPhenomenaList.forEach { presentAttributeRegister.add( it.sender.noumenon.pollRandomAttribute(it.externalPhenomenaImpression.type) ) }

    }

    //forms max-wisdom opinions based on registers
    fun reflect() {

    }


}