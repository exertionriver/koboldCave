package org.river.exertion.ai.noumena.other.being.humanoid

import org.river.exertion.ai.attribute.Trait.Companion.mergeOverrideTraits
import org.river.exertion.ai.attribute.IntelligenceAttribute.intelligenceRange
import org.river.exertion.ai.attribute.InternalStateAttribute.internalStateRange
import org.river.exertion.ai.internalFocus.InternalFocusInstance
import org.river.exertion.ai.noumena.IAttributeable
import org.river.exertion.ai.noumena.INoumenon
import org.river.exertion.ai.noumena.NoumenonType
import org.river.exertion.ai.noumena.other.being.HumanoidNoumenon
import org.river.exertion.ai.symbol.ISymbology
import org.river.exertion.ai.symbol.SymbolInstance
import org.river.exertion.ai.symbol.SymbolType

object LowRaceNoumenon : INoumenon, IAttributeable, ISymbology {

    override fun type() = NoumenonType.LOW_RACE
    override fun types() = HumanoidNoumenon.types().toMutableList().apply { this.add( type() ) }.toList()
    override fun traits() = HumanoidNoumenon.traits().mergeOverrideTraits(listOf(
        internalStateRange { noumenonObj = this@LowRaceNoumenon.javaClass; noumenonOrder = 3; minValue = 0.4f; maxValue = 0.6f },
        intelligenceRange { noumenonObj = this@LowRaceNoumenon.javaClass; noumenonOrder = 8; minValue = 6; maxValue = 8 }
    ))

    override var lexicon = mutableSetOf<SymbolType>()
    override var sourceInternalFocuses = mutableSetOf<InternalFocusInstance>()
}