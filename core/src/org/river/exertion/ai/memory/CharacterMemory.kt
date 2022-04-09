package org.river.exertion.ai.memory

import org.river.exertion.ai.internalState.InternalFacetInstancesState
import org.river.exertion.ai.internalFacet.SurpriseFacet.surpriseFacet
import org.river.exertion.ai.noumena.NoumenonType
import org.river.exertion.ai.perception.PerceivedAttribute
import org.river.exertion.ai.perception.PerceivedNoumenon
import org.river.exertion.btree.v0_1.IBTCharacter

class CharacterMemory {

    val registerExecutive = RegisterExecutive()
    val encounterMemory = EncounterMemory()
    val longtermMemory = LongtermMemory()

    val internalState = InternalFacetInstancesState()

    fun update(delta : Float, character : IBTCharacter) {

//poll attributes from external phenomena, store in regExec
        character.characterManifest.getExternalPhenomenaList().forEach { perceivedExternalPhenomenon ->
            val attributeInstance = perceivedExternalPhenomenon.sender!!.noumenonInstance.pollRandomAttribute(perceivedExternalPhenomenon.externalPhenomenaImpression!!.type)!! //poll random attribute not yet seen in encounter?
            val perceivedAttribute = PerceivedAttribute(attributeInstance, perceivedExternalPhenomenon)

            val noumenonTypes = perceivedExternalPhenomenon.sender.noumenonInstance.sourceNoumenon.types().plus(NoumenonType.INDIVIDUAL)
            var instanceName : String?

            noumenonTypes.forEach { noumenonType ->
                lateinit var perceivedNoumenon: PerceivedNoumenon
                instanceName = if (noumenonType == NoumenonType.INDIVIDUAL) perceivedExternalPhenomenon.sender.noumenonInstance.instanceName else null

                if (registerExecutive.noumenaRegister.none { it.noumenonType == noumenonType && (instanceName == null || (it.instanceName == instanceName) ) }) { //check longterm memory
                    if (longtermMemory.noumenaRegister.none { it.noumenonType == noumenonType && (instanceName == null || (it.instanceName == instanceName) ) }) { //not found in longterm memory, add
                        perceivedNoumenon = PerceivedNoumenon(internalStateInstance = internalState, knowledgeSourceInstance = KnowledgeSourceInstance(KnowledgeSourceType.EXPERIENCE)).apply { this.perceivedAttributes.add(perceivedAttribute); this.instanceName = instanceName; this.noumenonType = noumenonType; isNamed = true }
                        registerExecutive.noumenaRegister.add(perceivedNoumenon)
                        if (noumenonType != NoumenonType.INDIVIDUAL) this.internalState.add(surpriseFacet { magnitude = 0.3f }) //novel noumenon
                    } else { //noumenon found in longterm memory, pull over
                        perceivedNoumenon = longtermMemory.noumenaRegister.filter { it.noumenonType == noumenonType && (instanceName == null || (it.instanceName == instanceName) ) }.first()
                        registerExecutive.noumenaRegister.add(perceivedNoumenon)
                    }
                } else { //noumenon already in regExec
                    perceivedNoumenon = registerExecutive.noumenaRegister.filter { it.noumenonType == noumenonType && (instanceName == null || (it.instanceName == instanceName) ) }.first()
                }

                if (!perceivedNoumenon.perceivedAttributes.contains(perceivedAttribute)) { //if perceived attribute is new
                    perceivedNoumenon.perceivedAttributes.add(perceivedAttribute)
                    this.internalState.add(surpriseFacet { magnitude = 0.1f }) //novelty
                }
            }
        }
    }
}