package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import ktx.ashley.mapperFor
import org.river.exertion.ai.internalSymbol.core.SymbologyInstance
import org.river.exertion.ai.messaging.AbsentSymbolMessage
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.PresentSymbolMessage
import org.river.exertion.ecs.component.action.core.IComponent

class SymbologyComponent(var entity : Telegraph) : IComponent, Component, Telegraph {

    init {
        MessageManager.getInstance().addListener(this, MessageChannel.INT_PRESENT_SYMBOL_MODIFY.id())
        MessageManager.getInstance().addListener(this, MessageChannel.INT_PRESENT_SYMBOL_MODIFY_ALL.id())
        MessageManager.getInstance().addListener(this, MessageChannel.INT_PRESENT_SYMBOL_MODIFIED.id())
        MessageManager.getInstance().addListener(this, MessageChannel.INT_FOCUS.id())
    }

    override val componentName = "Symbology"

    var internalSymbology = SymbologyInstance()

    @Suppress("NewApi")
    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageChannel.INT_PRESENT_SYMBOL_MODIFY.id()) {
                val updateSymbolMessage = msg.extraInfo as PresentSymbolMessage
                internalSymbology.internalSymbolDisplay.symbolsPresent.firstOrNull { it == updateSymbolMessage.presentSymbolInstance }?.updatePosition(entity, updateSymbolMessage.deltaPosition)
            }
            if (msg.message == MessageChannel.INT_PRESENT_SYMBOL_MODIFY_ALL.id()) {
                val updateSymbolMessage = msg.extraInfo as PresentSymbolMessage
                internalSymbology.internalSymbolDisplay.symbolsPresent.filter { it.symbolObj == updateSymbolMessage.presentSymbolInstance.symbolObj }.forEach { it.updatePosition(entity, updateSymbolMessage.deltaPosition) }
            }
            if (msg.message == MessageChannel.INT_PRESENT_SYMBOL_MODIFIED.id()) {
                val updateSymbolMessage = msg.extraInfo as PresentSymbolMessage
                internalSymbology.internalSymbolDisplay.symbolsPresent.filter { it.symbolObj.presentModifiers.map { modifier -> modifier.modifyingSymbol }.contains(updateSymbolMessage.presentSymbolInstance.symbolObj) }.forEach { it.updateModifiedPosition(entity, updateSymbolMessage.presentSymbolInstance, updateSymbolMessage.deltaPosition) }
            }
            if (msg.message == MessageChannel.INT_ABSENT_SYMBOL_MODIFY.id()) {
                val updateSymbolMessage = msg.extraInfo as AbsentSymbolMessage
                internalSymbology.internalSymbolDisplay.symbolsAbsent.filter { it.symbolObj == updateSymbolMessage.absentSymbolInstance.symbolObj }.forEach { it.updatePosition(entity, updateSymbolMessage.deltaPosition) }
            }
            if (msg.message == MessageChannel.INT_PRESENT_SYMBOL_SPAWN.id()) {
                val updateSymbolMessage = msg.extraInfo as PresentSymbolMessage
                internalSymbology.internalSymbolDisplay.symbolsPresent.add(updateSymbolMessage.presentSymbolInstance)
            }
            if (msg.message == MessageChannel.INT_ABSENT_SYMBOL_SPAWN.id()) {
                val updateSymbolMessage = msg.extraInfo as AbsentSymbolMessage
                internalSymbology.internalSymbolDisplay.symbolsAbsent.add(updateSymbolMessage.absentSymbolInstance)
            }
            if (msg.message == MessageChannel.INT_PRESENT_SYMBOL_DESPAWN.id()) {
                val updateSymbolMessage = msg.extraInfo as PresentSymbolMessage
                internalSymbology.internalSymbolDisplay.symbolsPresent.removeIf { it.symbolObj == updateSymbolMessage.presentSymbolInstance.symbolObj }
            }
            if (msg.message == MessageChannel.INT_ABSENT_SYMBOL_DESPAWN.id()) {
                val updateSymbolMessage = msg.extraInfo as AbsentSymbolMessage
                internalSymbology.internalSymbolDisplay.symbolsAbsent.removeIf { it.symbolObj == updateSymbolMessage.absentSymbolInstance.symbolObj }
            }
            if (msg.message == MessageChannel.INT_FOCUS.id()) {

            }
        }
        return true
    }

    companion object {
        val mapper = mapperFor<SymbologyComponent>()

        fun has(entity : Entity) : Boolean = entity.components.firstOrNull{ it is SymbologyComponent } != null
        fun getFor(entity : Entity) : SymbologyComponent? = if (has(entity)) entity.components.first { it is SymbologyComponent } as SymbologyComponent else null

    }

}