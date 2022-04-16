package org.river.exertion.ai.symbol

import org.river.exertion.ai.internalFocus.InternalFocus
import org.river.exertion.ai.internalFocus.InternalFocusImpactor
import org.river.exertion.ai.internalFocus.InternalFocusType
import org.river.exertion.ai.noumena.KoboldNoumenon
import org.river.exertion.ai.symbol.Vision.Companion.vision
import org.river.exertion.ai.symbol.VisionImpactor.Companion.visionImpactor

object KoboldSymbology : ISymbology {

    override var beliefs = mutableSetOf<Belief>().apply { this.addAll(mutableSetOf(
        Belief(SymbolType.BEST_THING, Logic(SymbolType.BEST_THING, LogicType.AND, SymbolType.GOOD_HEALTH, SymbolType.KINSHIP), 0.7f),
        Belief(SymbolType.KINSHIP, KoboldNoumenon, 0.8f),
        Belief(SymbolType.MY_LIFE, SymbolType.GOOD_HEALTH, 0.9f),

        Belief(SymbolType.FOOD, "MushroomNoumenon", 0.3f),
        Belief(SymbolType.FOOD, "DeliciousBugNoumenon", 0.5f),
        Belief(SymbolType.FOOD, "TastiestLichenNoumenon", 0.9f)
    ) ) }

    override var beliefImpactors = mutableSetOf<BeliefImpactor>().apply { this.addAll(mutableSetOf(
        BeliefImpactor(SymbolType.KINSHIP, SymbolType.NOUMENON_BETRAYAL, -0.2f),
    ) ) }

    var logics = mutableSetOf<Logic>().apply {
        Logic(SymbolType.BEST_THING, LogicType.AND, SymbolType.GOOD_HEALTH, SymbolType.KINSHIP)
    }

    override var visions = mutableSetOf<Vision>().apply { this.addAll(mutableSetOf(
            Belief(SymbolType.WANT, SymbolType.SHINY_THING, 0.7f).vision(0.3f),
            Belief(SymbolType.NEED, SymbolType.FOOD, 0.7f).vision(0.3f)
    ) ) }

    override var visionImpactors = mutableSetOf<VisionImpactor>().apply { this.addAll(mutableSetOf(
        BeliefImpactor(SymbolType.FOOD, SymbolType.FOOD_CONSUMED, 0.0f).visionImpactor(1.0f)
    ) ) }

    override var internalFocuses = mutableSetOf<InternalFocus>().apply { this.addAll(mutableSetOf(
            InternalFocus(InternalFocusType.STRATEGY, SymbolType.FOOD, 0.2f, 0f)
    ) ) }

    override var internalFocusImpactors = mutableSetOf<InternalFocusImpactor>().apply { this.addAll(mutableSetOf(
            InternalFocusImpactor(InternalFocusType.QUEST, SymbolType.KINSHIP, SymbolType.CONTINUED_FAILURE, -0.05f, 0f)
    ) ) }

    fun generateInternalFocuses() {
        val visionSymbols = visions.map { it.referent as SymbolType }
        val currentInternalFocusSymbols = internalFocuses.map { it.targetSymbolType }

        val symbolsToGenerateFor = visionSymbols.filterNot { currentInternalFocusSymbols.contains(it) }
        symbolsToGenerateFor.forEach { internalFocuses.add(InternalFocus(InternalFocusType.STRATEGY, it, 0.5f, 0.0f)) }
    }

}



//if belief is not fulfilled, copy to vision

//visions ordered by conviction desc
//search beliefs by vision referent to see if present at vision conviction >= belief (1 - conviction), ordered by conviction desc
//if belief found with direct referent, generate internal focus to accomplish vision
//if belief found with symbol referent, repeat search for beliefs using symbol referent

//internal focus includes visionImpactors (conviction and context become important here)
//strategy: approach
//tactics: scan, approach objective, harvest, consume
//if no other context than 'mushroom' is perceptual or closer, approach mushrooms
//if no other context than 'mushroom' is social or closer, approach mushrooms
//...is familiar or closer, approach
//...is intimate or closer, approach
//kneel down, pick mushroom, eat mushroom, sit down to digest (tactics, strategy, vision accomplishment = 1)

//strategy: cautious approach
//tactics: scan, discern social context, evaluate risk / reward
//if other context than 'mushroom' is perceptual or closer, discern context
//if other social context is hostile, evaluate risk
//if other social context is indifferent, return to strategy 'approach'
//if other social context is friendly, signal friendliness

//strategy: hostile approach
//if other social context is hostile, approach mushrooms against hostile
//continue mushrooms approach against hostile until mushrooms intimate context
//kneel down, pick mushrooms, seek exit from hostile encounter (tactics, strategy = 1, vision = .8)

//strategy: friendly approach
//if other social context is friendly, signal invitation to mushrooms
//continue mushrooms approach until mushrooms intimate context
//kneel down, pick mushroom, eat mushroom, sit down to digest (tactics, strategy, vision accomplishment = 1)

//strategy: hostile retreat
//if other social context is hostile and dangerous, seek exit from hostile encounter
//wait at perceptual context for some time
//re-approach mushroom location to re-assess (tactics, strategy = 1, vision accomplishment = .3-ish)

//evaluating accomplishment of tactics / strategy wrt not fully accomplishing vision