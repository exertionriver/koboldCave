package org.river.exertion.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import ktx.ashley.allOf
import org.river.exertion.ai.internalFacet.SurpriseFacet
import org.river.exertion.ai.internalSymbol.perceivedSymbols.UnknownSymbol
import org.river.exertion.ai.memory.KnowledgeSourceInstance
import org.river.exertion.ai.memory.KnowledgeSourceType
import org.river.exertion.ai.memory.MemoryInstance
import org.river.exertion.ai.messaging.SymbolMessage
import org.river.exertion.ai.noumena.core.NoumenonType
import org.river.exertion.ai.perception.PerceivedAttribute
import org.river.exertion.ai.perception.PerceivedNoumenon
import org.river.exertion.ecs.component.IdentityComponent
import org.river.exertion.ecs.component.ManifestComponent
import org.river.exertion.ecs.component.MemoryComponent
import org.river.exertion.ecs.component.SymbologyComponent
import org.river.exertion.ecs.entity.IEntity

class MemorySystem : IntervalIteratingSystem(allOf(MemoryComponent::class).get(), 1/10f) {

    override fun processEntity(entity: Entity) {

        //poll attributes from external phenomena, store in activeMemory
        MemoryComponent.getFor(entity)!!.internalMemory.perceivedPhenomena.forEach { perceivedPhenomenon ->
            val attributeInstance = perceivedPhenomenon.perceivedExternalPhenomena!!.sender!!.noumenonInstance.pollRandomAttributeInstance(perceivedPhenomenon.perceivedExternalPhenomena!!.externalPhenomenaImpression!!.type)!! //poll random attribute not yet seen in encounter?
            val perceivedAttribute = PerceivedAttribute(attributeInstance, perceivedPhenomenon.perceivedExternalPhenomena)

            val noumenonTypes = perceivedPhenomenon.perceivedExternalPhenomena!!.sender!!.noumenonInstance.sourceNoumenon.types().plus(NoumenonType.INDIVIDUAL)
            var instanceName : String?

            noumenonTypes.forEach { noumenonType ->
                instanceName = if (noumenonType == NoumenonType.INDIVIDUAL) perceivedPhenomenon.perceivedExternalPhenomena!!.sender!!.noumenonInstance.instanceName else null

                if (MemoryComponent.getFor(entity)!!.internalMemory.activeMemory.noumenaRegister.none { it.perceivedNoumenon.noumenonType == noumenonType && (instanceName == null || (it.perceivedNoumenon.instanceName == instanceName) ) }) { //check active memory first
                    if (MemoryComponent.getFor(entity)!!.internalMemory.longtermMemory.noumenaRegister.none { it.perceivedNoumenon.noumenonType == noumenonType && (instanceName == null || (it.perceivedNoumenon.instanceName == instanceName) ) }) { //not found in active memory, check longterm memory
                        //add to active memory
                        val perceivedNoumenon = PerceivedNoumenon(knowledgeSourceInstance = KnowledgeSourceInstance(KnowledgeSourceType.EXPERIENCE)).apply { this.perceivedAttributes.add(perceivedAttribute); this.instanceName = instanceName; this.noumenonType = noumenonType; isNamed = true }
                        MemoryComponent.getFor(entity)!!.internalMemory.activeMemory.noumenaRegister.add(MemoryInstance(perceivedNoumenon, UnknownSymbol))
                        if (noumenonType == NoumenonType.INDIVIDUAL)
                            SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.spawn(SymbolMessage(symbolInstance = UnknownSymbol.spawn()))
                    } else { //noumenon found in longterm memory, pull over
                        val memoryInstance = MemoryComponent.getFor(entity)!!.internalMemory.longtermMemory.noumenaRegister.filter { it.perceivedNoumenon.noumenonType == noumenonType && (instanceName == null || (it.perceivedNoumenon.instanceName == instanceName) ) }.first()
                        MemoryComponent.getFor(entity)!!.internalMemory.activeMemory.noumenaRegister.add(memoryInstance.apply { this.perceivedNoumenon.perceivedAttributes.add(perceivedAttribute) ; this.symbol = memoryInstance.symbol})
                        if (noumenonType == NoumenonType.INDIVIDUAL)
                            SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.spawn(SymbolMessage(symbolInstance = memoryInstance.symbol.spawn()))
                    }
                } else { //noumenon already in active memory, update association; todo: merge association with past associations
           //         MemoryComponent.getFor(entity)!!.internalMemory.activeMemory.noumenaRegister.filter { it.perceivedNoumenon.noumenonType == noumenonType && (instanceName == null || (it.perceivedNoumenon.instanceName == instanceName) ) }.first().apply { this.perceivedNoumenon.perceivedAttributes.add(perceivedAttribute); this.internalFacetInstancesState = MemoryComponent.getFor(entity)!!.internalFacetInstancesState }
                }
            }
        }
    }
}
