package org.river.exertion.ai.internalFocus.internalFocuses

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.InternalFocusType
import org.river.exertion.ai.internalFocus.internalFocuses.ApproachFocus
import org.river.exertion.ai.internalFocus.internalFocuses.RetreatFocus
import org.river.exertion.ai.internalFocus.internalFocuses.proximity.*
import org.river.exertion.ai.internalSymbol.core.SymbolDisplayType
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.internalSymbol.core.SymbolTargetPosition
import org.river.exertion.ai.internalSymbol.ornaments.FamiliarOrnament
import org.river.exertion.ai.internalSymbol.ornaments.SocialOrnament
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage

object FriendFocus : IInternalFocus {

    override var tag = "friend focus"
    override var type = InternalFocusType.TACTIC
    override var momentMinimum = 0f

    override var satisfyingStrategies = mutableListOf<IInternalFocus>(
    )

    override fun satisfyingCondition(targetPresentSymbol : SymbolInstance) : Boolean {
        satisfyingStrategies.clear()
        var satisfied = false

        when {
            SymbolTargetPosition.gtTargetPosition(targetPresentSymbol.position, targetPresentSymbol.targetPosition) -> satisfyingStrategies.add(CloseFocus)
            SymbolTargetPosition.ltTargetPosition(targetPresentSymbol.position, targetPresentSymbol.targetPosition) -> satisfyingStrategies.add(OpenFocus)
            else -> satisfied = true
        }

        return satisfied
    }

    override fun satisfyingResult(entity: Telegraph, targetPresentSymbol : SymbolInstance) {
        MessageChannel.INT_SYMBOL_DESPAWN.send(entity, SymbolMessage(symbol = targetPresentSymbol.symbolObj, symbolDisplayType = SymbolDisplayType.ABSENT))
    }
}