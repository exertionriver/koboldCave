package org.river.exertion.ai.symbol

import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect
import org.river.exertion.ai.phenomena.ExternalPhenomenaType

interface ISymbol {

    var type : SymbolType
    var referent : ReferentType
    var presence : Float

}