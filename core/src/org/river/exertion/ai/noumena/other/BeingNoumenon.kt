package org.river.exertion.ai.noumena.other

import org.river.exertion.ai.attribute.Trait.Companion.mergeOverrideTraits
import org.river.exertion.ai.internalFacet.InternalFacetAttribute
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.InternalFocusDisplay
import org.river.exertion.ai.noumena.core.IAttributeable
import org.river.exertion.ai.noumena.core.INoumenon
import org.river.exertion.ai.noumena.core.NoumenonType
import org.river.exertion.ai.noumena.OtherNoumenon
import org.river.exertion.ai.internalSymbol.core.IInternalSymbol
import org.river.exertion.ai.internalSymbol.core.IInternalSymbology
import org.river.exertion.ai.internalSymbol.core.InternalSymbolDisplay

object BeingNoumenon : INoumenon, IAttributeable, IInternalSymbology {

    override fun type() = NoumenonType.BEING
    override fun types() = OtherNoumenon.types().toMutableList().apply { this.add(type()) }.toList()
    override fun traits() = OtherNoumenon.traits().mergeOverrideTraits(listOf())
    override fun facetAttributes() = mutableSetOf<InternalFacetAttribute>()

    override var internalSymbolLexicon = mutableSetOf<IInternalSymbol>()
    override var internalFocusesLexicon = mutableSetOf<IInternalFocus>()

    override var internalSymbolDisplay = InternalSymbolDisplay()
    override var internalFocusDisplay = InternalFocusDisplay()
}