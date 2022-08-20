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
            targetPresentSymbol.ornaments.contains(FamiliarOrnament) ->
                if (SymbolTargetPosition.STABILIZE_FAMILIAR.gtTargetPosition(targetPresentSymbol.position))
                    satisfyingStrategies.add(CloseFamiliarFocus)
                else if (SymbolTargetPosition.STABILIZE_FAMILIAR.ltTargetPosition(targetPresentSymbol.position))
                    satisfyingStrategies.add(OpenFamiliarFocus)
                else satisfied = true
            targetPresentSymbol.ornaments.contains(SocialOrnament) ->
                if (SymbolTargetPosition.STABILIZE_SOCIAL.gtTargetPosition(targetPresentSymbol.position))
                    satisfyingStrategies.add(CloseSocialFocus)
                else if (SymbolTargetPosition.STABILIZE_SOCIAL.ltTargetPosition(targetPresentSymbol.position))
                    satisfyingStrategies.add(OpenSocialFocus)
                else satisfied = true
        }

        return satisfied
    }

    override fun satisfyingResult(entity: Telegraph, targetPresentSymbol : SymbolInstance) {
        MessageChannel.INT_SYMBOL_DESPAWN.send(entity, SymbolMessage(symbol = targetPresentSymbol.symbolObj, symbolDisplayType = SymbolDisplayType.ABSENT))
    }
}