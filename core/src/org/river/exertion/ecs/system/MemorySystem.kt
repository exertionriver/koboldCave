package org.river.exertion.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import ktx.ashley.allOf
import org.river.exertion.ai.internalFacet.SurpriseFacet
import org.river.exertion.ai.memory.KnowledgeSourceInstance
import org.river.exertion.ai.memory.KnowledgeSourceType
import org.river.exertion.ai.noumena.core.NoumenonType
import org.river.exertion.ai.perception.PerceivedAttribute
import org.river.exertion.ai.perception.PerceivedNoumenon
import org.river.exertion.ecs.component.IdentityComponent
import org.river.exertion.ecs.component.ManifestComponent
import org.river.exertion.ecs.component.MemoryComponent
import org.river.exertion.ecs.entity.IEntity

class MemorySystem : IntervalIteratingSystem(allOf(MemoryComponent::class).get(), 1/10f) {

    override fun processEntity(entity: Entity) {
        val delta = 1/10f

//update internal state, from internal anxiety
//update internal state, from regExec

//poll attributes from external phenomena, store in regExec

            MemoryComponent.getFor(entity)!!.perceivedPhenomena.forEach { perceivedPhenomenon ->
                val attributeInstance = perceivedPhenomenon.perceivedExternalPhenomena!!.sender!!.noumenonInstance.pollRandomAttributeInstance(perceivedPhenomenon.perceivedExternalPhenomena!!.externalPhenomenaImpression!!.type)!! //poll random attribute not yet seen in encounter?
                val perceivedAttribute = PerceivedAttribute(attributeInstance, perceivedPhenomenon.perceivedExternalPhenomena)

                val noumenonTypes = perceivedPhenomenon.perceivedExternalPhenomena!!.sender!!.noumenonInstance.sourceNoumenon.types().plus(NoumenonType.INDIVIDUAL)
                var instanceName : String?

                noumenonTypes.forEach { noumenonType ->
                    lateinit var perceivedNoumenon: PerceivedNoumenon
                    instanceName = if (noumenonType == NoumenonType.INDIVIDUAL) perceivedPhenomenon.perceivedExternalPhenomena!!.sender!!.noumenonInstance.instanceName else null

                    if (MemoryComponent.getFor(entity)!!.internalMemory.activeMemory.noumenaRegister.none { it.noumenonType == noumenonType && (instanceName == null || (it.instanceName == instanceName) ) }) { //check longterm memory
                        if (MemoryComponent.getFor(entity)!!.internalMemory.longtermMemory.noumenaRegister.none { it.noumenonType == noumenonType && (instanceName == null || (it.instanceName == instanceName) ) }) { //not found in longterm memory, add
                            perceivedNoumenon = PerceivedNoumenon(knowledgeSourceInstance = KnowledgeSourceInstance(KnowledgeSourceType.EXPERIENCE)).apply { this.perceivedAttributes.add(perceivedAttribute); this.instanceName = instanceName; this.noumenonType = noumenonType; isNamed = true }
                            MemoryComponent.getFor(entity)!!.internalMemory.activeMemory.noumenaRegister.add(perceivedNoumenon)
                      //      if (noumenonType != NoumenonType.INDIVIDUAL) MemoryComponent.getFor(entity)!!.internalMemory.internalState.add(SurpriseFacet.surpriseFacet { magnitude = 0.3f }) //novel noumenon
                        } else { //noumenon found in longterm memory, pull over
                            perceivedNoumenon = MemoryComponent.getFor(entity)!!.internalMemory.longtermMemory.noumenaRegister.filter { it.noumenonType == noumenonType && (instanceName == null || (it.instanceName == instanceName) ) }.first()
                            MemoryComponent.getFor(entity)!!.internalMemory.activeMemory.noumenaRegister.add(perceivedNoumenon)
                        }
                    } else { //noumenon already in regExec
                        perceivedNoumenon = MemoryComponent.getFor(entity)!!.internalMemory.activeMemory.noumenaRegister.filter { it.noumenonType == noumenonType && (instanceName == null || (it.instanceName == instanceName) ) }.first()
                    }

                    if (!perceivedNoumenon.perceivedAttributes.contains(perceivedAttribute)) { //if perceived attribute is new
                        perceivedNoumenon.perceivedAttributes.add(perceivedAttribute)
//                        MemoryComponent.getFor(entity)!!.internalMemory.internalState.add(SurpriseFacet.surpriseFacet { magnitude = 0.1f }) //novelty
                    }
                }
            }
    }
}
