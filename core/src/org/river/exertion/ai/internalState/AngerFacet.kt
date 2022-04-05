package org.river.exertion.ai.internalState

import org.river.exertion.ai.activities.ContendActivity

object AngerFacet : IInternalFacet, ContendActivity {

    override val type = InternalFacetType.ANGER

    fun angerFacet(lambda : InternalFacetInstance.() -> Unit) = InternalFacetInstance(facetObj = AngerFacet.javaClass).apply(lambda)

    override fun strengthIncreases(): IInternalFacet {
        TODO("Not yet implemented")
    }

    override fun strengthDescreases(): IInternalFacet {
        TODO("Not yet implemented")
    }

    override fun weaknessIncreases(): IInternalFacet {
        TODO("Not yet implemented")
    }

    override fun weaknessDecreases(): IInternalFacet {
        TODO("Not yet implemented")
    }

    override fun threatIncreases(): IInternalFacet {
        TODO("Not yet implemented")
    }

    override fun threatDescreases(): IInternalFacet {
        TODO("Not yet implemented")
    }

    override fun opportunityIncreases(): IInternalFacet {
        TODO("Not yet implemented")
    }

    override fun opportunityDecreases(): IInternalFacet {
        TODO("Not yet implemented")
    }

}