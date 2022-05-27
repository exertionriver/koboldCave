package org.river.exertion.ai.noumena.other.being.humanoid.low_race

import org.river.exertion.ai.attribute.GrowlAttribute.growlRange
import org.river.exertion.ai.attribute.IntelligenceAttribute.intelligenceRange
import org.river.exertion.ai.attribute.Trait.Companion.mergeOverrideTraits
import org.river.exertion.ai.internalFacet.*
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.InternalFocusDisplay
import org.river.exertion.ai.noumena.other.being.humanoid.LowRaceNoumenon
import org.river.exertion.ai.internalSymbol.core.IInternalSymbol
import org.river.exertion.ai.internalSymbol.core.IInternalSymbology
import org.river.exertion.ai.internalSymbol.core.InternalSymbolDisplay
import org.river.exertion.ai.noumena.core.*
import java.util.*

object KoboldNoumenon : INoumenon, InstantiatableNoumenon, IAttributeable, IInternalSymbology {

    override fun type() = NoumenonType.KOBOLD
    override fun types() = LowRaceNoumenon.types().toMutableList().apply { this.add(type()) }.toList()
    override fun traits() = LowRaceNoumenon.traits().mergeOverrideTraits( listOf(
        growlRange { noumenonObj = this@KoboldNoumenon.javaClass; noumenonOrder = 2; minValue = type().tag(); maxValue = type().tag() },
        intelligenceRange { noumenonObj = this@KoboldNoumenon.javaClass; noumenonOrder = 8; minValue = 7; maxValue = 8 }
    ))
    override fun facetAttributes() = mutableSetOf(
            InternalFacetAttribute.internalFacetAttribute { internalFacetInstance = ConfusionFacet.confusionFacet {}; origin = 0.2f; arising = 0.5f },
            InternalFacetAttribute.internalFacetAttribute { internalFacetInstance = AngerFacet.angerFacet {}; origin = 0.3f; arising = 0.7f },
            InternalFacetAttribute.internalFacetAttribute { internalFacetInstance = FearFacet.fearFacet {}; origin = 0.4f; arising = 0.8f },
            InternalFacetAttribute.internalFacetAttribute { internalFacetInstance = DoubtFacet.doubtFacet {}; origin = 0f; arising = 0.1f }
    )

    override var internalSymbolLexicon = mutableSetOf<IInternalSymbol>()
    override var internalFocusesLexicon = mutableSetOf<IInternalFocus>()

    override var internalSymbolDisplay = InternalSymbolDisplay()
    override var internalFocusDisplay = InternalFocusDisplay()

    fun kobold(lambda : NoumenonInstance.() -> Unit) = NoumenonInstance(sourceNoumenonType = KoboldNoumenon.javaClass, instanceName = "razza" + Random().nextInt()).apply(lambda)

}