package org.river.exertion.btree.v0_1

class CharacterManifest {

    val externalMagnitudeMinTreshold = .3f
    val externalMagnitudeOtherTreshold = .6f
    val externalMagnitudeThreatOppTreshold = .8f

    val listMax = 10

    val perceptionList = MutableList<ExternalPhenomenaImpression?>(listMax) { null }
    val projectionList = MutableList<InternalPhenomenaInstance?>(listMax) { null }

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
}