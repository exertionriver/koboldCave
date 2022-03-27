package org.river.exertion.ai.manifest

import org.river.exertion.ai.phenomena.ExternalPhenomenaImpression
import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression
import org.river.exertion.btree.v0_1.IBTCharacter

interface IManifest {

    val listMax : Int

    val manifestType : ExternalPhenomenaType

    val perceptionList : MutableList<Pair<IBTCharacter, ExternalPhenomenaImpression>?>
    val projectionList : MutableList<InternalPhenomenaImpression?>

    fun joinedList() : MutableList<Pair<Pair<IBTCharacter, ExternalPhenomenaImpression>?, InternalPhenomenaImpression?>> {
        val returnList : MutableList<Pair<Pair<IBTCharacter, ExternalPhenomenaImpression>?, InternalPhenomenaImpression?>> = mutableListOf()

        (0 until listMax).forEach { idx ->
            returnList.add(idx, Pair(perceptionList[idx], projectionList[idx]))
        }

        return returnList
    }

    fun addImpression(sender : IBTCharacter, externalPhenomenaImpression: ExternalPhenomenaImpression) {
        var checkCounter = 0
        var foundSlot = false
        val idxList = List(listMax) {idx -> idx}.shuffled()


        while ( (checkCounter < listMax) && !foundSlot ) {
            if (perceptionList[idxList[checkCounter]] == null) {
                perceptionList[idxList[checkCounter]] = Pair(sender, externalPhenomenaImpression)
                foundSlot = true
            }

            checkCounter++
        }

        if (!foundSlot) {
            perceptionList[perceptionList.indexOf(perceptionList.minByOrNull{ it!!.second.countdown })] = Pair(sender, externalPhenomenaImpression)
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