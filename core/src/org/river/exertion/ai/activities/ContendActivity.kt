package org.river.exertion.ai.activities

import org.river.exertion.ai.internalState.IInternalFacet

interface ContendActivity {

    fun strengthIncreases() : IInternalFacet
    fun strengthDescreases() : IInternalFacet
    fun weaknessIncreases() : IInternalFacet
    fun weaknessDecreases() : IInternalFacet
    fun threatIncreases() : IInternalFacet
    fun threatDescreases() : IInternalFacet
    fun opportunityIncreases() : IInternalFacet
    fun opportunityDecreases() : IInternalFacet

}