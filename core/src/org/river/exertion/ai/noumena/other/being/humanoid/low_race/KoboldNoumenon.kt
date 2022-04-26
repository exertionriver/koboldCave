package org.river.exertion.ai.noumena.other.being.humanoid.low_race

import org.river.exertion.ai.attribute.GrowlAttribute.growlRange
import org.river.exertion.ai.attribute.IntelligenceAttribute.intelligenceRange
import org.river.exertion.ai.attribute.InternalStateAttribute.internalStateRange
import org.river.exertion.ai.attribute.Trait.Companion.mergeOverrideTraits
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.InternalFocusDisplay
import org.river.exertion.ai.noumena.*
import org.river.exertion.ai.noumena.other.being.humanoid.LowRaceNoumenon
import org.river.exertion.ai.symbol.ISymbol
import org.river.exertion.ai.symbol.ISymbology
import org.river.exertion.ai.symbol.SymbolDisplay
import java.util.*

object KoboldNoumenon : INoumenon, InstantiatableNoumenon, IAttributeable, ISymbology {

    override fun type() = NoumenonType.KOBOLD
    override fun types() = LowRaceNoumenon.types().toMutableList().apply { this.add(type()) }.toList()
    override fun traits() = LowRaceNoumenon.traits().mergeOverrideTraits( listOf(
        growlRange { noumenonObj = this@KoboldNoumenon.javaClass; noumenonOrder = 2; minValue = type().tag(); maxValue = type().tag() },
        internalStateRange { noumenonObj = this@KoboldNoumenon.javaClass; noumenonOrder = 3; minValue = 0.5f; maxValue = 0.6f },
        intelligenceRange { noumenonObj = this@KoboldNoumenon.javaClass; noumenonOrder = 8; minValue = 7; maxValue = 8 }
    ))

    override var symbolLexicon = mutableSetOf<ISymbol>()
    override var internalFocusesLexicon = mutableSetOf<IInternalFocus>()

    override var symbolDisplay = SymbolDisplay()
    override var internalFocusDisplay = InternalFocusDisplay()

    fun kobold(lambda : NoumenonInstance.() -> Unit) = NoumenonInstance(sourceNoumenonType = KoboldNoumenon.javaClass, instanceName = "razza" + Random().nextInt()).apply(lambda)

}