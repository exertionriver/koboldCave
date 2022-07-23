package org.river.exertion.ai.manifest

import org.river.exertion.ai.perception.PerceivedPhenomena
import org.river.exertion.ai.perception.PerceivedExternalPhenomena
import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression

interface IManifest {

    fun listMax() = listMax

    val manifestType : ExternalPhenomenaType

    val perceptionList : MutableList<PerceivedExternalPhenomena?>
    val projectionList : MutableList<InternalPhenomenaImpression?>

    fun joinedList() : MutableList<PerceivedPhenomena> {
        val returnList : MutableList<PerceivedPhenomena> = mutableListOf()

        (0 until listMax()).forEach { idx ->
            returnList.add(PerceivedPhenomena(PerceivedExternalPhenomena(perceptionList[idx]?.sender, perceptionList[idx]?.externalPhenomenaImpression), projectionList[idx]))
        }

        return returnList
    }

    fun addImpression(perceivedExternalPhenomena: PerceivedExternalPhenomena) {
        var checkCounter = 0
        var foundSlot = false
        val idxList = List(listMax()) {idx -> idx}.shuffled()

        while ( (checkCounter < listMax()) && !foundSlot ) {
            if (perceptionList[idxList[checkCounter]] == null) {
                perceptionList[idxList[checkCounter]] = perceivedExternalPhenomena
                foundSlot = true
            }

            checkCounter++
        }

        if (!foundSlot) {
            perceptionList[perceptionList.indexOf(perceptionList.minByOrNull{ it!!.externalPhenomenaImpression!!.countdown })] = perceivedExternalPhenomena
        }
    }

    fun addImpression(internalPhenomenaImpression: InternalPhenomenaImpression) {
        var checkCounter = 0
        var foundSlot = false
        val idxList = List(listMax()) {idx -> idx}.shuffled()

        while ( (checkCounter < listMax()) && !foundSlot ) {
            if (projectionList[idxList[checkCounter]] == null) {
                projectionList[idxList[checkCounter]] = internalPhenomenaImpression
                foundSlot = true
            }

            checkCounter++
        }

        if (!foundSlot) {
            projectionList[projectionList.indexOf(projectionList.minByOrNull{ it!!.countdown })] = internalPhenomenaImpression
        }
    }

    companion object {
        val listMax = 10
    }
}