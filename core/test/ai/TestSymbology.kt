package ai

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.math.Vector3
import org.junit.jupiter.api.Test
import org.river.exertion.MessageIds
import org.river.exertion.ai.internalFacet.AngerFacet.angerFacet
import org.river.exertion.ai.internalFacet.ConfusionFacet.confusionFacet
import org.river.exertion.ai.internalFacet.DoubtFacet.doubtFacet
import org.river.exertion.ai.internalFacet.FearFacet.fearFacet
import org.river.exertion.ai.internalFacet.InternalFacetAttribute
import org.river.exertion.ai.internalFacet.InternalFacetAttribute.Companion.internalFacetAttribute
import org.river.exertion.ai.internalFacet.InternalFacetInstance
import org.river.exertion.ai.internalFacet.NoneFacet
import org.river.exertion.ai.internalState.InternalFacetAttributesState
import org.river.exertion.ai.phenomena.ExternalPhenomenaInstance
import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance
import org.river.exertion.ai.symbol.KoboldSymbology
import org.river.exertion.btree.v0_1.*


@ExperimentalUnsignedTypes
class TestSymbology {

    @Test
    fun testPopulateInternalFocuses() {

        println("before generating:")
        KoboldSymbology.internalFocuses.forEach { println(it) }
        KoboldSymbology.generateInternalFocuses()

        println("after generating:")
        KoboldSymbology.internalFocuses.forEach { println(it) }

    }
}