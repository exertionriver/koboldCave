package org.river.exertion.ai.messaging

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.perception.PerceivedExternalPhenomena
import org.river.exertion.ai.phenomena.ExternalPhenomenaInstance
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance
import org.river.exertion.ecs.component.action.ActionMoveComponent
import kotlin.reflect.KClass

enum class MessageChannel {

    ECS_S2D_BRIDGE { override val messageClass = ActionMoveComponent::class },
    S2D_ECS_BRIDGE { override val messageClass = String::class },
    ECS_FSM_BRIDGE { override val messageClass = String::class },
    PLAN_BRIDGE { override val messageClass = String::class },
    PERCEPTION_BRIDGE { override val messageClass = String::class },
    FEELING_BRIDGE { override val messageClass = String::class },
    UI_TIMING_DISPLAY { override val messageClass = TimingTableMessage::class },
    UI_FOCUS_DISPLAY { override val messageClass = FocusDisplayMessage::class },
    UI_FACET_DISPLAY { override val messageClass = FacetTableMessage::class },
    UI_SYMBOL_DISPLAY { override val messageClass = SymbolDisplayMessage::class },
    UI_ANXIETY_BAR { override val messageClass = AnxietyBarMessage::class },
    UI_MANIFEST_DISPLAY { override val messageClass = ManifestDisplayMessage::class },
    UI_MEMORY_DISPLAY { override val messageClass = MemoryDisplayMessage::class },
    CURNODE_BRIDGE { override val messageClass = ActionMoveComponent::class },
    NODEROOMMESH_BRIDGE { override val messageClass = ActionMoveComponent::class },
    LOSMAP_BRIDGE { override val messageClass = ActionMoveComponent::class },
    ADD_EXT_PHENOMENA { override val messageClass = ExternalPhenomenaInstance::class },
    ADD_INT_PHENOMENA { override val messageClass = InternalPhenomenaInstance::class },
    REMOVE_EXT_PHENOMENA { override val messageClass = PerceivedExternalPhenomena::class },
    REMOVE_INT_PHENOMENA { override val messageClass = InternalPhenomenaImpression::class },
    INT_CONDITION { override val messageClass = Float::class },
    INT_FACET { override val messageClass = FacetMessage::class },
    INT_MEMORY { override val messageClass = PerceivedPhenomenaMessage::class },
    INT_SYMBOL_MODIFY { override val messageClass = SymbolMessage::class },
    INT_SYMBOL_SPAWN { override val messageClass = SymbolMessage::class },
    INT_SYMBOL_DESPAWN { override val messageClass = SymbolMessage::class },
    INT_SYMBOL_ADD_ORNAMENT { override val messageClass = OrnamentMessage::class },
    INT_SYMBOL_REMOVE_ORNAMENT { override val messageClass = OrnamentMessage::class },
    INT_ADD_FOCUS_PLAN { override val messageClass = FocusMessage::class },
    INT_REMOVE_FOCUS_PLAN { override val messageClass = FocusMessage::class },
    INT_ADD_FOCUS_CHAIN_LINK { override val messageClass = FocusMessage::class },
    INT_REMOVE_FOCUS_CHAIN_LINK { override val messageClass = FocusMessage::class },
    INT_PHENOMENA_FACETS { override val messageClass = FacetImpressionMessage::class },
    INT_MEMORY_FACETS { override val messageClass = FocusMessage::class },
    INT_FACET_MODIFY { override val messageClass = FacetMessage::class },
    ;

    fun id() = this.ordinal
    abstract val messageClass : KClass<*>
    fun send(sender: Telegraph?, message : Any) = if (this.messageClass.isInstance(message)) MessageManager.getInstance().dispatchMessage(sender, this.id(), message) else throw Exception("send:$this requires ${this.messageClass}, found ${message::class}")
    fun enableReceive(receiver: Telegraph?) = MessageManager.getInstance().addListener(receiver, this.id())
    inline fun <reified T:Any> receiveMessage(message : Any) : T {
        return if (T::class == this.messageClass) message as T else throw Exception("receive:$this requires ${this.messageClass}, found ${T::class}")
    }
}