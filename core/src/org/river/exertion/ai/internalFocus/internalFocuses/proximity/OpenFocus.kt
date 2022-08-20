package org.river.exertion.ai.internalFocus.internalFocuses.proximity

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.InternalFocusType
import org.river.exertion.ai.internalFocus.internalFocuses.ApproachFocus
import org.river.exertion.ai.internalFocus.internalFocuses.RetreatFocus
import org.river.exertion.ai.internalSymbol.core.SymbolDisplayType
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.internalSymbol.ornaments.FamiliarOrnament
import org.river.exertion.ai.internalSymbol.ornaments.SocialOrnament
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage

object OpenFocus : IInternalFocus {

    override var tag = "open focus"
    override var type = InternalFocusType.SENSING
    override var momentMinimum = 0f

    override var satisfyingStrategies = mutableListOf<IInternalFocus>(
        RetreatFocus
    )

    override fun satisfyingCondition(targetPresentSymbol : SymbolInstance) =
        when {
            targetPresentSymbol.ornaments.contains(FamiliarOrnament) -> OpenFamiliarFocus.satisfyingCondition(targetPresentSymbol)
            targetPresentSymbol.ornaments.contains(SocialOrnament) -> OpenSocialFocus.satisfyingCondition(targetPresentSymbol)
            else -> OpenPerceptualFocus.satisfyingCondition(targetPresentSymbol)
        }

    override fun satisfyingResult(entity: Telegraph, targetPresentSymbol : SymbolInstance) {
        MessageChannel.INT_SYMBOL_DESPAWN.send(entity, SymbolMessage(symbol = targetPresentSymbol.symbolObj, symbolDisplayType = SymbolDisplayType.ABSENT))
    }
}