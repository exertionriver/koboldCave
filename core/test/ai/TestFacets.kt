package ai

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.math.Vector3
import org.junit.jupiter.api.Test
import org.river.exertion.MessageIds
import org.river.exertion.ai.internalFacet.AngerFacet.angerFacet
import org.river.exertion.ai.internalFacet.ConfusionFacet.confusionFacet
import org.river.exertion.ai.internalFacet.DoubtFacet.doubtFacet
import org.river.exertion.ai.internalFacet.FearFacet.fearFacet
import org.river.exertion.ai.internalFacet.InternalFacetAttribute.Companion.internalFacetAttribute
import org.river.exertion.ai.internalState.InternalFacetAttributesState
import org.river.exertion.ai.phenomena.ExternalPhenomenaInstance
import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance
import org.river.exertion.ecs.component.ConditionComponent
import org.river.exertion.ecs.component.FacetComponent
import org.river.exertion.ecs.component.ManifestComponent
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.component.action.ActionSimpleDecideMoveComponent
import org.river.exertion.ecs.entity.character.CharacterKobold
import org.river.exertion.ecs.system.SystemManager


@ExperimentalUnsignedTypes
class TestFacets {

    val engine = PooledEngine().apply { SystemManager.init(this) }
    val character = CharacterKobold.ecsInstantiate(engine).apply { this.remove(ActionMoveComponent.getFor(this)!!.javaClass) ; this.remove(ActionSimpleDecideMoveComponent.getFor(this)!!.javaClass) }
    val secondCharacter = CharacterKobold.ecsInstantiate(engine).apply { this.remove(ActionMoveComponent.getFor(this)!!.javaClass) ; this.remove(ActionSimpleDecideMoveComponent.getFor(this)!!.javaClass) }

    @Test
    fun testOriginArisingProjections() {
        val testState = InternalFacetAttributesState().apply { this.internalFacetAttributes = mutableSetOf(
            internalFacetAttribute { internalFacetInstance = confusionFacet {}; origin = 0.2f; arising = 0.5f },
            internalFacetAttribute { internalFacetInstance = angerFacet {}; origin = 0.3f; arising = 0.7f },
            internalFacetAttribute { internalFacetInstance = fearFacet {}; origin = 0.4f; arising = 0.8f },
            internalFacetAttribute { internalFacetInstance = doubtFacet {}; origin = 0f; arising = 0.1f }
        ) }

        FacetComponent.getFor(character)!!.arisingInternalState = testState

        (0..10).forEach { mAnxiety ->
            println("mAnxiety : ${mAnxiety / 10f}")
            testState.projections(mAnxiety / 10f).forEachIndexed { idx, it -> println("slot($idx) : ${it?.arisenFacet?.facet()?.type?.tag()}, ${it?.arisenFacet?.magnitude}") }
        }
    }

    @Test
    fun testOriginArisingECSProjections() {
        val testState = InternalFacetAttributesState().apply { this.internalFacetAttributes = mutableSetOf(
                internalFacetAttribute { internalFacetInstance = confusionFacet {}; origin = 0.2f; arising = 0.5f },
                internalFacetAttribute { internalFacetInstance = angerFacet {}; origin = 0.3f; arising = 0.7f },
                internalFacetAttribute { internalFacetInstance = fearFacet {}; origin = 0.4f; arising = 0.8f },
                internalFacetAttribute { internalFacetInstance = doubtFacet {}; origin = 0f; arising = 0.1f }
        ) }

        ConditionComponent.getFor(character)!!.mIntAnxiety = .5f

        FacetComponent.getFor(character)!!.arisingInternalState = testState

        (0..10).forEach { mAnxiety ->
            engine.update(CharacterKobold.getFor(character)!!.moment )

            println("mIntAnxiety: ${FacetComponent.getFor(character)!!.mInternalAnxiety}")

            ManifestComponent.getFor(character)!!.internalManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.internalPhenomenaImpression?.countdown}") }
        }
    }

}