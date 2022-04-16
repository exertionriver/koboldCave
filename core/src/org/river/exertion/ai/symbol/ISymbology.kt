package org.river.exertion.ai.symbol

import org.river.exertion.ai.internalFocus.InternalFocusImpactor
import org.river.exertion.ai.internalFocus.InternalFocus

interface ISymbology {

    //current state
    var beliefs : MutableSet<Belief>

    var beliefImpactors : MutableSet<BeliefImpactor>

    //future state, needs, wants, objectives
    var visions : MutableSet<Vision>

    var visionImpactors : MutableSet<VisionImpactor>

    //methods for getting to vision from belief
    var internalFocuses : MutableSet<InternalFocus>

    var internalFocusImpactors : MutableSet<InternalFocusImpactor>

    //patterns?
}