package org.river.exertion.ai.symbol

import org.river.exertion.ai.internalFocus.*
import org.river.exertion.ai.noumena.other.being.fungus.lichen.LichenNoumenon
import org.river.exertion.ai.noumena.other.being.fungus.mushroom.MushroomNoumenon

object KoboldSymbology : ISymbology  {

    override var lexicon = mutableSetOf<SymbolType>().apply { this.addAll(SymbolType.values()) }

    override var sourceInternalFocuses = mutableSetOf(
//        belief { symbolInstance { type = SymbolType.FOOD; referent = MushroomNoumenon }; conviction = 0.3f },
//        belief { symbolInstance { type = SymbolType.FOOD; referent = LichenNoumenon }; conviction = 0.5f },
        logic { resultSymbolType = SymbolType.BEST_THING; ILogic.LogicType.AND; SymbolType.GOOD_HEALTH; SymbolType.KINSHIP },
        need { targetSymbolType = SymbolType.FOOD; triggerSymbolType = SymbolType.HUNGER; satisfactionSymbolType = SymbolType.FOOD_CONSUMED },
        want { targetSymbolType = SymbolType.SHINY_THING; triggerSymbolType = SymbolType.SHINY_THING; satisfactionSymbolType = SymbolType.SHINY_THING_OBTAINED },
    )

//    override var callInternalFocuses: MutableSet<InternalFocusInstance> = mutableSetOf(
 //   )

//    override var responseInternalFocuses: MutableSet<InternalFocusInstance> = mutableSetOf()

    fun MutableSet<InternalFocusInstance>.updateNeed(internalFocusInstance : InternalFocusInstance) {
        this.filter { it.type == InternalFocusType.NEED && (it.instance as NeedInstance).targetSymbolType == (internalFocusInstance.instance as NeedInstance).targetSymbolType }.forEach {
            (it.instance as NeedInstance).urgency = (internalFocusInstance.instance as NeedInstance).urgency
        }
    }

    fun MutableSet<InternalFocusInstance>.generateNeedTargets(symbols : MutableSet<SymbolType>, internalFocuses : MutableSet<InternalFocusInstance>, symbolDisplay : MutableSet<SymbolInstance> ) {

//        val targetsToAdd = mutableSetOf<InternalFocusInstance>()

        val symbolTypesToProcess = symbolDisplay//.filter { symbols.contains(it.type) }.map { it.type }

        val targets = this.filter { it.type == InternalFocusType.TARGET }.map { it.instance }.toSet() as Set<TargetInstance>

        val needs = internalFocuses.filter { it.type == InternalFocusType.NEED }.map { it.instance }.toSet() as Set<NeedInstance>
        val needsToTrigger = needs.filter { needInstance -> symbolTypesToProcess.contains( needInstance.triggerSymbolType) }

        needsToTrigger.filterNot { it.expressedIn(targets) }.forEach {
            this.add( target { }.apply { this.instance = it.expressedAsTargetInstance() } )
        }
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