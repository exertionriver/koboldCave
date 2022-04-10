package org.river.exertion.ai.activity

import org.river.exertion.ai.internalFacet.IInternalFacet

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