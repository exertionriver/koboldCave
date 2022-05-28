package org.river.exertion.ai.internalSymbol.core

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.perceivedSymbols.NonePerceivedSymbol
import org.river.exertion.ai.messaging.AbsentSymbolMessage
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.PresentSymbolMessage
import kotlin.math.sign

data class PresentSymbolInstance (var symbolObj : IPerceivedSymbol = NonePerceivedSymbol, var cycles : Float = 0f, var position : Float = 0f) {

    var consumeCapacity = 0f
    var handleCapacity = 0f
    var possessCapacity = 0f

    var ornaments = mutableSetOf<ControlSymbolInstance>()

    fun normalizePosition(deltaPosition : Float) {

        this.position += deltaPosition

        //first update position wrt symbol cycle style
        if (this.symbolObj.cycle == SymbolCycle.MULTIPLE) {
            while (this.position < 0) {
                this.position += 1
                this.cycles -= 1
            }
            while (this.position > 1) {
                this.position -= 1
                this.cycles += 1
            }
        } else { //single or none
            if (this.position < 0) {
                this.position = 0f
                this.cycles = 0f
            }
            if (this.position > 1) {
                this.position = 1f
                this.cycles = 0f
            }
        }
    }

    //directly update position
    fun updatePosition(entity : Telegraph, deltaPosition : Float) {
        normalizePosition(deltaPosition)
        spawnDespawn(entity)
        MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_PRESENT_SYMBOL_MODIFIED.id(), PresentSymbolMessage(this, deltaPosition))
    }

    //indirectly update position via modification
    //e.g. this == Hunger, modifyingSymbol == Food
    fun updateModifiedPosition(entity : Telegraph, modifyingSymbol : PresentSymbolInstance, deltaPosition : Float) {

        val modifierEntry = this.symbolObj.presentModifiers.first { it.modifyingSymbol == modifyingSymbol.symbolObj }

        val modifiedPosition = this.symbolObj.targetMagnetism.targetPosition().sign *
                modifyingSymbol.symbolObj.targetMagnetism.targetPosition().sign *
                modifierEntry.modifierRatio *
                deltaPosition

        normalizePosition(modifiedPosition)
        spawnDespawn(entity)
        MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_PRESENT_SYMBOL_MODIFIED.id(), PresentSymbolMessage(this, modifiedPosition))
    }

    fun spawnDespawn(entity : Telegraph) {

        //spawn present symbols
        this.symbolObj.spawnsPresent.filter { it.thresholdType == SymbolThresholdType.LESS_THAN && this.position < it.position }.forEach {
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_PRESENT_SYMBOL_SPAWN.id(), PresentSymbolMessage(it.spawnSymbol.spawnPresent(), 0f))
        }
        this.symbolObj.spawnsPresent.filter { it.thresholdType == SymbolThresholdType.GREATER_THAN && this.position > it.position }.forEach {
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_PRESENT_SYMBOL_SPAWN.id(), PresentSymbolMessage(it.spawnSymbol.spawnPresent(), 0f))
        }

        //spawn absent symbols
        this.symbolObj.spawnsAbsent.filter { it.thresholdType == SymbolThresholdType.LESS_THAN && this.position < it.position }.forEach {
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_ABSENT_SYMBOL_SPAWN.id(), AbsentSymbolMessage(it.spawnSymbol.spawnAbsent(), 0f))
        }
        this.symbolObj.spawnsAbsent.filter { it.thresholdType == SymbolThresholdType.GREATER_THAN && this.position > it.position }.forEach {
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_ABSENT_SYMBOL_SPAWN.id(), AbsentSymbolMessage(it.spawnSymbol.spawnAbsent(), 0f))
        }

        //despawn present symbols
        this.symbolObj.despawnsPresent.filter { it.thresholdType == SymbolThresholdType.LESS_THAN && this.position < it.position }.forEach {
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_PRESENT_SYMBOL_DESPAWN.id(), PresentSymbolMessage(it.spawnSymbol.spawnPresent(), 0f))
        }
        this.symbolObj.despawnsPresent.filter { it.thresholdType == SymbolThresholdType.GREATER_THAN && this.position > it.position }.forEach {
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_PRESENT_SYMBOL_DESPAWN.id(), PresentSymbolMessage(it.spawnSymbol.spawnPresent(), 0f))
        }

        //despawn absent symbols
        this.symbolObj.despawnsAbsent.filter { it.thresholdType == SymbolThresholdType.LESS_THAN && this.position < it.position }.forEach {
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_ABSENT_SYMBOL_DESPAWN.id(), AbsentSymbolMessage(it.spawnSymbol.spawnAbsent(), 0f))
        }
        this.symbolObj.despawnsAbsent.filter { it.thresholdType == SymbolThresholdType.GREATER_THAN && this.position > it.position }.forEach {
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_ABSENT_SYMBOL_DESPAWN.id(), AbsentSymbolMessage(it.spawnSymbol.spawnAbsent(), 0f))
        }
    }
}