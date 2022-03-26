package org.river.exertion.ai

class CharacterManifest {

    val externalMagnitudeMinTreshold = .3f
    val externalMagnitudeOtherTreshold = .6f
    val externalMagnitudeThreatOppTreshold = .8f

    val listMax = 10

    val perceptionList = MutableList<ExternalPhenomenaImpression?>(listMax) { null }
    val projectionList = MutableList<InternalPhenomenaImpression?>(listMax) { null }

    fun joinedList() : MutableList<Pair<ExternalPhenomenaImpression?, InternalPhenomenaImpression?>> {
        val returnList : MutableList<Pair<ExternalPhenomenaImpression?, InternalPhenomenaImpression?>> = mutableListOf()

        (0 until listMax).forEach { idx ->
            returnList.add(idx, Pair(perceptionList[idx], projectionList[idx]))
        }

        return returnList
    }

    fun addImpression(externalPhenomenaImpression: ExternalPhenomenaImpression) {
        var checkCounter = 0
        var foundSlot = false
        val idxList = List(listMax) {idx -> idx}.shuffled()


        while ( (checkCounter < listMax) && !foundSlot ) {
            if (perceptionList[idxList[checkCounter]] == null) {
                perceptionList[idxList[checkCounter]] = externalPhenomenaImpression
                foundSlot = true
            }

            checkCounter++
        }

        if (!foundSlot) {
            perceptionList[perceptionList.indexOf(perceptionList.minByOrNull{ it!!.countdown })] = externalPhenomenaImpression
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