package org.river.exertion.ai.memory

import org.river.exertion.ai.internalState.InternalStateInstance
import org.river.exertion.ai.perception.PerceivedAttribute
import org.river.exertion.ai.perception.PerceivedNoumenon
import org.river.exertion.btree.v0_1.IBTCharacter

class CharacterMemory {

    val registerExecutive = RegisterExecutive()
    val encounterMemory = EncounterMemory()
    val longtermMemory = LongtermMemory()

    val internalState = InternalStateInstance()

    fun update(delta : Float, character : IBTCharacter) {

//update internal state?

//merge regExec memory into encounterMemory, 'perfumed' with internal State

//clear regExit memory
        registerExecutive.noumenaRegister.clear()

//poll attributes from external phenomena, store in regExec
        character.characterManifest.getExternalPhenomenaList().forEach {
            val attributeInstance = it.sender!!.noumenonInstance.pollRandomAttribute(it.externalPhenomenaImpression!!.type)!! //poll random attribute not yet seen in encounter?
            val perceivedAttribute = PerceivedAttribute(attributeInstance, it)

                it.sender.noumenonInstance.sourceNoumenon.types().forEach { noumenonType ->
                    val perceivedNoumenon = PerceivedNoumenon(internalStateInstance = internalState, knowledgeSourceInstance = KnowledgeSourceInstance(KnowledgeSourceType.EXPERIENCE) ).apply { this.perceivedAttributes.add(perceivedAttribute); this.noumenonType = noumenonType; isNamed = true}

                    registerExecutive.noumenaRegister.add(perceivedNoumenon)
                }
            }
        }

}