package org.river.exertion.ai.noumena.other.being.humanoid.low_race

import org.river.exertion.ai.attribute.GrowlAttribute.growlRange
import org.river.exertion.ai.attribute.IntelligenceAttribute.intelligenceRange
import org.river.exertion.ai.attribute.Trait.Companion.mergeOverrideTraits
import org.river.exertion.ai.internalFacet.*
import org.river.exertion.ai.noumena.other.being.humanoid.LowRaceNoumenon
import org.river.exertion.ai.noumena.core.*
import java.util.*

object KoboldNoumenon : INoumenon, InstantiatableNoumenon, IAttributeable {

    override fun type() = NoumenonType.KOBOLD
    override fun types() = LowRaceNoumenon.types().toMutableList().apply { this.add(type()) }.toList()
    override fun traits() = LowRaceNoumenon.traits().mergeOverrideTraits( listOf(
        growlRange { noumenonObj = this@KoboldNoumenon; noumenonOrder = 2; minValue = type().tag(); maxValue = type().tag() },
        intelligenceRange { noumenonObj = this@KoboldNoumenon; noumenonOrder = 8; minValue = 7; maxValue = 8 }
    ))
    override fun facetAttributes() = mutableSetOf(
            //todo: clean up to remove instantiation
            InternalFacetAttribute.internalFacetAttribute { facetObj = ConfusionFacet; origin = 0.2f; arising = 0.5f },
            InternalFacetAttribute.internalFacetAttribute { facetObj = AngerFacet; origin = 0.3f; arising = 0.7f },
            InternalFacetAttribute.internalFacetAttribute { facetObj = FearFacet; origin = 0.4f; arising = 0.8f },
            InternalFacetAttribute.internalFacetAttribute { facetObj = DoubtFacet; origin = 0f; arising = 0.1f }
    )

    fun kobold(lambda : NoumenonInstance.() -> Unit) = NoumenonInstance(sourceNoumenon = KoboldNoumenon, instanceName = "razza" + Random().nextInt()).apply(lambda)

}