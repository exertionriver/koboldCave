package org.river.exertion.ai.noumena.other

import org.river.exertion.ai.attribute.Trait.Companion.mergeOverrideTraits
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.InternalFocusDisplay
import org.river.exertion.ai.internalFocus.InternalFocusInstance
import org.river.exertion.ai.noumena.IAttributeable
import org.river.exertion.ai.noumena.INoumenon
import org.river.exertion.ai.noumena.NoumenonType
import org.river.exertion.ai.noumena.OtherNoumenon
import org.river.exertion.ai.symbol.ISymbol
import org.river.exertion.ai.symbol.ISymbology
import org.river.exertion.ai.symbol.SymbolDisplay

object BeingNoumenon : INoumenon, IAttributeable, ISymbology {

    override fun type() = NoumenonType.BEING
    override fun types() = OtherNoumenon.types().toMutableList().apply { this.add(type()) }.toList()
    override fun traits() = OtherNoumenon.traits().mergeOverrideTraits(listOf())

    override var symbolLexicon = mutableSetOf<ISymbol>()
    override var internalFocusesLexicon = mutableSetOf<IInternalFocus>()

    override var symbolDisplay = SymbolDisplay()
    override var internalFocusDisplay = InternalFocusDisplay()
}