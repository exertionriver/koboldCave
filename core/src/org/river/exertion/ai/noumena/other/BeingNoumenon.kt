package org.river.exertion.ai.noumena.other

import org.river.exertion.ai.attribute.Trait.Companion.mergeOverrideTraits
import org.river.exertion.ai.internalFocus.InternalFocusInstance
import org.river.exertion.ai.noumena.IAttributeable
import org.river.exertion.ai.noumena.INoumenon
import org.river.exertion.ai.noumena.NoumenonType
import org.river.exertion.ai.noumena.OtherNoumenon
import org.river.exertion.ai.symbol.ISymbology
import org.river.exertion.ai.symbol.SymbolInstance
import org.river.exertion.ai.symbol.SymbolType

object BeingNoumenon : INoumenon, IAttributeable, ISymbology {

    override fun type() = NoumenonType.BEING
    override fun types() = OtherNoumenon.types().toMutableList().apply { this.add(type()) }.toList()
    override fun traits() = OtherNoumenon.traits().mergeOverrideTraits(listOf())

    override var symbols = mutableSetOf<SymbolType>()
    override var internalFocuses = mutableSetOf<InternalFocusInstance>()
}