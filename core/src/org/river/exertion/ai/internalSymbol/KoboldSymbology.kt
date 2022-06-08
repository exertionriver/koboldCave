package org.river.exertion.ai.internalSymbol

object KoboldSymbology {

 //   override var internalSymbolLexicon = mutableSetOf<IInternalSymbol>()
 //   override var internalFocusesLexicon = mutableSetOf<IInternalFocus>()

//    override var internalSymbolDisplay = InternalSymbolDisplay()
//    override var internalFocusDisplay = InternalFocusDisplay()
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