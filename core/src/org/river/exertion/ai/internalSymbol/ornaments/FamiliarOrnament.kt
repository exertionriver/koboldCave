package org.river.exertion.ai.internalSymbol.ornaments

import org.river.exertion.ai.internalFacet.FearFacet
import org.river.exertion.ai.internalSymbol.core.*

object FamiliarOrnament : IPerceivedSymbolOrnament {

    override var tag = "familiar ornament"
    override var type = SymbolType.ORNAMENT
    override var baseTargetPosition = SymbolTargetPosition.STABILIZE_FAMILIAR
    override var facetModifiers = mutableSetOf<FacetModifier>(FacetModifier(FearFacet, -.15f))
}