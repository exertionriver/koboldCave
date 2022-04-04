package org.river.exertion.ai.internalState

import org.river.exertion.ai.internalState.NoneState.noneState

data class InternalStateInstance(val internalState: IInternalState, var magnitude : Float) {

    operator fun plus(other : InternalStateInstance) : Set<InternalStateInstance> =
        if (this.internalState == other.internalState)
            setOf(this.apply { this.magnitude += other.magnitude } )
        else
            setOf(this, other)

    operator fun minus(other : InternalStateInstance) : Set<InternalStateInstance> =
            if (this.internalState == other.internalState) {
                val minVal = this.magnitude - other.magnitude
                if
                    (minVal > 0) setOf(this.apply { magnitude = minVal } )
                else
                    setOf(this.apply { magnitude = 0f } )
            }
            else
                setOf(this, other)

    companion object {
        fun none() = InternalStateInstance(NoneState, 0f)

        fun Set<InternalStateInstance>.mergePlus(other: Set<InternalStateInstance>): Set<InternalStateInstance> {
            val returnList = mutableSetOf<InternalStateInstance>()

            val thisStates = this.map { it.internalState.tag }
            val otherStates = other.map { it.internalState.tag }

            this.forEach { thisStateInstance ->
                //intersection
                if (otherStates.contains(thisStateInstance.internalState.tag)) {
                    val sharedOther = other.filter { it.internalState.tag == thisStateInstance.internalState.tag }.first()
                    returnList.add((thisStateInstance + sharedOther).first())
                } else { //rest of this
                    returnList.add(thisStateInstance)
                }
            }

            other.forEach { otherStateInstance ->
                //rest of other
                if (!thisStates.contains(otherStateInstance.internalState.tag)) {
                    returnList.add(otherStateInstance)
                }
            }

            return returnList
        }

        fun Set<InternalStateInstance>.mergeAvg(other: Set<InternalStateInstance>, avgBy : Int): Set<InternalStateInstance> {
            val returnList = mutableSetOf<InternalStateInstance>()

            val thisStates = this.map { it.internalState.tag }
            val otherStates = other.map { it.internalState.tag }

            this.forEach { thisStateInstance ->
                //intersection
                if (otherStates.contains(thisStateInstance.internalState.tag)) {
                    val sharedOther = other.filter { it.internalState.tag == thisStateInstance.internalState.tag }.first()
                    returnList.add(Pair(thisStateInstance, sharedOther).avgBy(avgBy))
                } else { //rest of this
                    returnList.add(thisStateInstance)
                }
            }

            other.forEach { otherStateInstance ->
                //rest of other
                if (!thisStates.contains(otherStateInstance.internalState.tag)) {
                    returnList.add(otherStateInstance)
                }
            }

            return returnList
        }

        fun Set<InternalStateInstance>.mergeMinus(other: Set<InternalStateInstance>): Set<InternalStateInstance> {
            val returnList = mutableSetOf<InternalStateInstance>()

            val thisStates = this.map { it.internalState.tag }
            val otherStates = other.map { it.internalState.tag }

            this.forEach { thisStateInstance ->
                //intersection
                if (otherStates.contains(thisStateInstance.internalState.tag)) {
                    val sharedOther = other.filter { it.internalState.tag == thisStateInstance.internalState.tag }.first()
                    returnList.add((thisStateInstance - sharedOther).first())
                } else { //rest of this
                    returnList.add(thisStateInstance)
                }
            }

            other.forEach { otherStateInstance ->
                //rest of other
                if (!thisStates.contains(otherStateInstance.internalState.tag)) {
                    returnList.add(otherStateInstance)
                }
            }

            return returnList
        }

        fun Pair<InternalStateInstance, InternalStateInstance>.avgBy(denom : Int) : InternalStateInstance =
            InternalStateInstance(this.first.internalState, (this@avgBy.first.magnitude + this@avgBy.second.magnitude) / denom)

        fun Set<InternalStateInstance>.magnitudeOpinion() : InternalStateInstance = if (this.isEmpty()) noneState {} else this.maxByOrNull { it.magnitude }!!
    }

}

