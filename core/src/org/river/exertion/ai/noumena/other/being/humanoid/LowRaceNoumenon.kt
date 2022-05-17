package org.river.exertion.ai.noumena.other.being.humanoid

import org.river.exertion.ai.attribute.Trait.Companion.mergeOverrideTraits
import org.river.exertion.ai.attribute.IntelligenceAttribute.intelligenceRange
import org.river.exertion.ai.attribute.InternalStateAttribute.internalStateRange
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.InternalFocusDisplay
import org.river.exertion.ai.noumena.core.IAttributeable
import org.river.exertion.ai.noumena.core.INoumenon
import org.river.exertion.ai.noumena.core.NoumenonType
import org.river.exertion.ai.noumena.other.being.HumanoidNoumenon
import org.river.exertion.ai.internalSymbol.core.IInternalSymbol
import org.river.exertion.ai.internalSymbol.core.IInternalSymbology
import org.river.exertion.ai.internalSymbol.core.InternalSymbolDisplay

object LowRaceNoumenon : INoumenon, IAttributeable, IInternalSymbology {

    override fun type() = NoumenonType.LOW_RACE
    override fun types() = HumanoidNoumenon.types().toMutableList().apply { this.add( type() ) }.toList()
    override fun traits() = HumanoidNoumenon.traits().mergeOverrideTraits(listOf(
        internalStateRange { noumenonObj = this@LowRaceNoumenon.javaClass; noumenonOrder = 3; minValue = 0.4f; maxValue = 0.6f },
        intelligenceRange { noumenonObj = this@LowRaceNoumenon.javaClass; noumenonOrder = 8; minValue = 6; maxValue = 8 }
    ))

    override var internalSymbolLexicon = mutableSetOf<IInternalSymbol>()
    override var internalFocusesLexicon = mutableSetOf<IInternalFocus>()

    override var internalSymbolDisplay = InternalSymbolDisplay()
    override var internalFocusDisplay = InternalFocusDisplay()
}