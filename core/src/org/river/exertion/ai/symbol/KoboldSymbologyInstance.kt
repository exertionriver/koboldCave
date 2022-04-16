package org.river.exertion.ai.symbol

import org.river.exertion.ai.condition.ConditionInstance
import org.river.exertion.ai.internalFocus.InternalFocus
import org.river.exertion.ai.internalFocus.InternalFocusImpactor
import org.river.exertion.ai.noumena.KoboldNoumenon
import org.river.exertion.ai.symbol.Vision.Companion.vision
import org.river.exertion.ai.symbol.VisionImpactor.Companion.visionImpactor

class KoboldSymbologyInstance(var conditionInstance : ConditionInstance) : ISymbology {

    override var beliefs = mutableSetOf<Belief>().apply { this.addAll(KoboldSymbology.beliefs) }

    override var beliefImpactors = mutableSetOf<BeliefImpactor>().apply { this.addAll(KoboldSymbology.beliefImpactors) }

    override var visions = mutableSetOf<Vision>().apply { this.addAll(mutableSetOf(
        Belief(SymbolType.WANT, SymbolType.SHINY_THING, 0.7f).vision(0.3f),
        Belief(SymbolType.NEED, SymbolType.FOOD, 0.7f).vision(0.3f)
    ) ) }

    override var visionImpactors = mutableSetOf<VisionImpactor>().apply { this.addAll(KoboldSymbology.visionImpactors) }

    override var internalFocuses = mutableSetOf<InternalFocus>().apply { this.addAll(KoboldSymbology.internalFocuses) }

    override var internalFocusImpactors = mutableSetOf<InternalFocusImpactor>().apply { this.addAll(KoboldSymbology.internalFocusImpactors) }
}