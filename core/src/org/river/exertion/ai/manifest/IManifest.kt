package org.river.exertion.ai.manifest

import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression

interface IManifest {

    val listMax : Int

    val manifestType : ExternalPhenomenaType

    val perceptionList : MutableList<PerceivedPhenomena?>
    val projectionList : MutableList<InternalPhenomenaImpression?>

    fun joinedList() : MutableList<PerceivedJoinedPhenomena> {
        val returnList : MutableList<PerceivedJoinedPhenomena> = mutableListOf()

        (0 until listMax).forEach { idx ->
            returnList.add(PerceivedJoinedPhenomena(perceptionList[idx]?.sender, perceptionList[idx]?.externalPhenomenaImpression, projectionList[idx]))
        }

        return returnList
    }

    fun addImpression(perceivedPhenomena: PerceivedPhenomena) {
        var checkCounter = 0
        var foundSlot = false
        val idxList = List(listMax) {idx -> idx}.shuffled()


        while ( (checkCounter < listMax) && !foundSlot ) {
            if (perceptionList[idxList[checkCounter]] == null) {
                perceptionList[idxList[checkCounter]] = perceivedPhenomena
                foundSlot = true
            }

            checkCounter++
        }

        if (!foundSlot) {
            perceptionList[perceptionList.indexOf(perceptionList.minByOrNull{ it!!.externalPhenomenaImpression.countdown })] = perceivedPhenomena
        }
    }

    fun addImpression(internalPhenomenaImpression: InternalPhenomenaImpression) {
        var checkCounter = 0
        var foundSlot = false
        val idxList = List(listMax) {idx -> idx}.shuffled()


        while ( (checkCounter < listMax) && !foundSlot ) {
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
}