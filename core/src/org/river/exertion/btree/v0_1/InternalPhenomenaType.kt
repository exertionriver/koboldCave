package org.river.exertion.btree.v0_1

enum class InternalPhenomenaType {

    PASSION {
        override fun plus(internalPhenomenaType: InternalPhenomenaType) : InternalPhenomenaType =
            when (internalPhenomenaType) {
                PASSION -> ENVY
                AGGRESSION -> JEALOUSY
                IGNORANCE -> DOUBT
                else -> DELUSION
            }
    },
    ENVY,
    JEALOUSY,
    DOUBT,
    AGGRESSION {
        override fun plus(internalPhenomenaType: InternalPhenomenaType) : InternalPhenomenaType =
                when (internalPhenomenaType) {
                    PASSION -> RAGE
                    AGGRESSION -> FEAR
                    IGNORANCE -> ARROGANCE
                    else -> DELUSION
                }
    },
    ARROGANCE,
    FEAR,
    RAGE,
    IGNORANCE {
        override fun plus(internalPhenomenaType: InternalPhenomenaType) : InternalPhenomenaType =
                when (internalPhenomenaType) {
                    PASSION -> NOSTALGIA
                    AGGRESSION -> SADNESS
                    IGNORANCE -> STUPIDITY
                    else -> DELUSION
                }
    },
    SADNESS,
    STUPIDITY,
    NOSTALGIA,
    DELUSION,
    NONE
    ;

    open fun plus(internalPhenomenaType: InternalPhenomenaType) : InternalPhenomenaType = DELUSION
}