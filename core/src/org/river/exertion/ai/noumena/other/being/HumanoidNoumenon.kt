package org.river.exertion.ai.noumena.other.being

import org.river.exertion.ai.attribute.Trait.Companion.mergeOverrideTraits
import org.river.exertion.ai.internalFocus.ILogic
import org.river.exertion.ai.internalFocus.InternalFocusInstance
import org.river.exertion.ai.noumena.IAttributeable
import org.river.exertion.ai.noumena.INoumenon
import org.river.exertion.ai.noumena.NoumenonType
import org.river.exertion.ai.noumena.other.BeingNoumenon
import org.river.exertion.ai.noumena.other.being.fungus.lichen.LichenNoumenon
import org.river.exertion.ai.noumena.other.being.fungus.mushroom.MushroomNoumenon
import org.river.exertion.ai.symbol.ISymbology
import org.river.exertion.ai.symbol.SymbolInstance
import org.river.exertion.ai.symbol.SymbolType

object HumanoidNoumenon : INoumenon, IAttributeable, ISymbology {

    override fun type() = NoumenonType.HUMANOID
    override fun types() = BeingNoumenon.types().toMutableList().apply { this.add(type()) }.toList()
    override fun traits() = BeingNoumenon.traits().mergeOverrideTraits(listOf())

    override var lexicon = mutableSetOf<SymbolType>()
    override var sourceInternalFocuses = mutableSetOf<InternalFocusInstance>()
}