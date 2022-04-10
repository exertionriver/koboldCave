package org.river.exertion.ai.noumena

import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect
import org.river.exertion.ai.attribute.*
import org.river.exertion.ai.attribute.Characteristic.Companion.getRandomCharacteristicAttributeInstances
import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.ai.property.PropertyInstance
import org.river.exertion.ai.property.Quality.Companion.getRandomQualityPropertyInstances

class NoumenonInstance(sourceNoumenonType : Class<InstantiatableNoumenon>, var instanceName : String) {

    val sourceNoumenon: InstantiatableNoumenon = sourceNoumenonType.kotlin.objectInstance!!
    var attributeInstances = sourceNoumenon.characteristics().getRandomCharacteristicAttributeInstances()
    var propertyInstances = sourceNoumenon.qualities().getRandomQualityPropertyInstances()

    private fun filteredAttributes(externalPhenomenaType: ExternalPhenomenaType?) =
        if (externalPhenomenaType == null) attributeInstances else attributeInstances.filter { attrInst -> attrInst.attribute().howPerceived() == externalPhenomenaType }

    private fun filteredProperties(externalPhenomenaType: ExternalPhenomenaType?) =
            if (externalPhenomenaType == null) propertyInstances else propertyInstances.filter { propInst -> propInst.property().howPerceived() == externalPhenomenaType }

    fun pollRandomAttributeInstance(externalPhenomenaType: ExternalPhenomenaType? = null) : AttributeInstance<*>? {
        val filteredAttributes = filteredAttributes(externalPhenomenaType)

        return if (filteredAttributes.isNotEmpty()) {
            return ProbabilitySelect( filteredAttributes.map { it }.associateWith { Probability(100f / filteredAttributes.size, 0f) } ).getSelectedProbability()!!
        } else null
    }

    fun pollRandomPropertyInstance(externalPhenomenaType: ExternalPhenomenaType? = null) : PropertyInstance<*>? {
        val filteredProperties = filteredProperties(externalPhenomenaType)

        return if (filteredProperties.isNotEmpty()) {
            return ProbabilitySelect( filteredProperties.map { it }.associateWith { Probability(100f / filteredProperties.size, 0f) } ).getSelectedProbability()!!
        } else null
    }
}