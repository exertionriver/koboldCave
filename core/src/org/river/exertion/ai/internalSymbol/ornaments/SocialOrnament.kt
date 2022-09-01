package org.river.exertion.ai.internalSymbol.ornaments

import org.river.exertion.ai.internalFacet.FearFacet
import org.river.exertion.ai.internalSymbol.core.*

object SocialOrnament : IPerceivedSymbolOrnament {

    override var tag = "social ornament"
    override var type = SymbolType.ORNAMENT
    override var baseTargetPosition = SymbolTargetPosition.STABILIZE_SOCIAL
    override var facetModifiers = mutableSetOf<FacetModifier>(FacetModifier(FearFacet, -.1f))
}