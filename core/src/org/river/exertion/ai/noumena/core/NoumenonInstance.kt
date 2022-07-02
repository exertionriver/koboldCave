package org.river.exertion.ai.noumena.core

import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect
import org.river.exertion.ai.attribute.*
import org.river.exertion.ai.attribute.Trait.Companion.getRandomCharacteristics
import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.ai.property.PropertyInstance
import org.river.exertion.ai.property.Quality.Companion.getRandomFeatures

class NoumenonInstance(val sourceNoumenon : InstantiatableNoumenon, var instanceName : String) {

//    val sourceNoumenon: InstantiatableNoumenon = sourceNoumenonType.kotlin.objectInstance!!
    var characteristics = if (sourceNoumenon is IAttributeable) sourceNoumenon.traits().getRandomCharacteristics() else null
    var facetAttributes = if (sourceNoumenon is IAttributeable) sourceNoumenon.facetAttributes() else null

    var features = if (sourceNoumenon is IPropertyable) sourceNoumenon.qualities().getRandomFeatures() else null

    private fun filteredAttributes(externalPhenomenaType: ExternalPhenomenaType?) =
        if (externalPhenomenaType == null) characteristics else characteristics?.filter { attrInst -> attrInst.attributeObj.howPerceived() == externalPhenomenaType }

    private fun filteredProperties(externalPhenomenaType: ExternalPhenomenaType?) =
            if (externalPhenomenaType == null) features else features?.filter { propInst -> propInst.propertyObj.howPerceived() == externalPhenomenaType }

    fun pollRandomAttributeInstance(externalPhenomenaType: ExternalPhenomenaType? = null) : Characteristic<*>? {
        val filteredAttributes = filteredAttributes(externalPhenomenaType)

        return if (filteredAttributes?.isNotEmpty() == true) {
            return ProbabilitySelect( filteredAttributes.map { it }.associateWith { Probability(100f / filteredAttributes.size, 0f) } ).getSelectedProbability()!!
        } else null
    }

    fun pollRandomPropertyInstance(externalPhenomenaType: ExternalPhenomenaType? = null) : PropertyInstance<*>? {
        val filteredProperties = filteredProperties(externalPhenomenaType)

        return if (filteredProperties?.isNotEmpty() == true) {
            return ProbabilitySelect( filteredProperties.map { it }.associateWith { Probability(100f / filteredProperties.size, 0f) } ).getSelectedProbability()!!
        } else null
    }
}