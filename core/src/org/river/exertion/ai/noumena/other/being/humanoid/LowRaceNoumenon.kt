package org.river.exertion.ai.noumena.other.being.humanoid

import org.river.exertion.ai.attribute.Trait.Companion.mergeOverrideTraits
import org.river.exertion.ai.attribute.IntelligenceAttribute.intelligenceRange
import org.river.exertion.ai.internalFacet.InternalFacetAttribute
import org.river.exertion.ai.noumena.core.IAttributeable
import org.river.exertion.ai.noumena.core.INoumenon
import org.river.exertion.ai.noumena.core.NoumenonType
import org.river.exertion.ai.noumena.other.being.HumanoidNoumenon

object LowRaceNoumenon : INoumenon, IAttributeable {

    override fun type() = NoumenonType.LOW_RACE
    override fun types() = HumanoidNoumenon.types().toMutableList().apply { this.add( type() ) }.toList()
    override fun traits() = HumanoidNoumenon.traits().mergeOverrideTraits(listOf(
        intelligenceRange { noumenonObj = this@LowRaceNoumenon.javaClass; noumenonOrder = 8; minValue = 6; maxValue = 8 }
    ))
    override fun facetAttributes() = mutableSetOf<InternalFacetAttribute>()

}