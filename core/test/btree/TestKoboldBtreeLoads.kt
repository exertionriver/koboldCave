package btree

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.decorator.Include
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser
import com.badlogic.gdx.ai.msg.Telegraph
import org.junit.jupiter.api.Test
import org.river.exertion.btree.v0_1.IBTCharacter
import org.river.exertion.btree.v0_1.KoboldCharacter
import org.river.exertion.btree.v0_1.NoneCharacter
import org.river.exertion.btree.v0_1.task_cond.HasExternalStimCondition
import org.river.exertion.btree.v0_1.task_cond.HasInternalStimCondition
import org.river.exertion.btree.v0_1.task_cond.HasRecognitionCondition
import org.river.exertion.geom.lattice.ArrayLattice
import org.river.exertion.geom.lattice.RoundedLattice
import java.io.FileReader
import java.nio.file.Paths


@ExperimentalUnsignedTypes
class TestKoboldBtreeLoads {

    var character = KoboldCharacter()

    val rootLocation = "../android/assets/btree/entity/entity_v0_1_root.btree"
    val notAbsorbedSubtreePath = "../android/assets/btree/entity/entity_v0_1_notAbsorbed.btree"
    val internalAbsorbedSubtreePath = "../android/assets/btree/entity/entity_v0_1_internalAbsorbed.btree"
    val externalAbsorbedSubtreePath = "../android/assets/btree/entity/entity_v0_1_externalAbsorbed.btree"

    fun notAbsorbedSubtreeLocation() = character.tree.getChild(0).getChild(0).getChild(0)
    fun internalAbsorbedSubtreeLocation() = character.tree.getChild(0).getChild(1)
    fun externalAbsorbedSubtreeLocation() = character.tree.getChild(0).getChild(2)

    //        addEncounterSubtree("android/assets/btree/kobold/kobold_encounter_v0_1.btree")
//        addInternalAbsorbedInternalActionSubtree("android/assets/btree/kobold/kobold_iaias_v0_1.btree")
//        addInternalAbsorbedExternalActionSubtree("android/assets/btree/kobold/kobold_iaeas_v0_1.btree")

    @Test
    fun testLoadRoot() {
        (0..10).forEach { idx ->
            when (idx) {
                0 -> { character.mIntAnxiety = 0f; character.mExtAnxiety = 0f; character.hasRecognition = false }
                1 -> { character.mIntAnxiety = 0.1f; character.mExtAnxiety = 0.1f }
                2 -> { character.mIntAnxiety = 0.19f; character.mExtAnxiety = 0.2f }
                3 -> { character.mIntAnxiety = 0.2f; character.mExtAnxiety = 0.2f }
                4 -> { character.mIntAnxiety = 0.3f; character.mExtAnxiety = 0.29f }
                5 -> { character.mIntAnxiety = 0.3f; character.mExtAnxiety = 0.3f }
                6 -> { character.mIntAnxiety = 0.4f; character.mExtAnxiety = 0.4f }
                7 -> { character.hasRecognition = true }
                8 -> { character.mIntAnxiety = 0.3f; character.mExtAnxiety = 0.29f }
                9 -> { character.mIntAnxiety = 0.19f; character.mExtAnxiety = 0.2f }
                10 -> { character.mIntAnxiety = 0.1f; character.mExtAnxiety = 0.1f }
            }
            println("(character measures) : intX:${character.mIntAnxiety}, extX:${character.mExtAnxiety}, awake:${character.mAwake}")
            character.tree.step()
            println("")
        }
    }
/*
    @Test
    fun testLoadNotAbsorbed() {
        initRoot()
        initNotAbsorbedSubtree()
        (0..10).forEach { idx ->
            when (idx) {
                0 -> { character.mIntAnxiety = 0f; character.mExtAnxiety = 0f; character.hasRecognition = false }
                1 -> { character.mIntAnxiety = 0.1f; character.mExtAnxiety = 0.1f }
                2 -> { character.mIntAnxiety = 0.19f; character.mExtAnxiety = 0.2f }
                3 -> { character.mIntAnxiety = 0.2f; character.mExtAnxiety = 0.2f }
                4 -> { character.mIntAnxiety = 0.3f; character.mExtAnxiety = 0.29f }
                5 -> { character.mIntAnxiety = 0.3f; character.mExtAnxiety = 0.3f }
                6 -> { character.mIntAnxiety = 0.4f; character.mExtAnxiety = 0.4f }
                7 -> { character.hasRecognition = true }
                8 -> { character.mIntAnxiety = 0.3f; character.mExtAnxiety = 0.29f }
                9 -> { character.mIntAnxiety = 0.19f; character.mExtAnxiety = 0.2f }
                10 -> { character.mIntAnxiety = 0.1f; character.mExtAnxiety = 0.1f }
            }
            println("(character measures) : intX:${character.mIntAnxiety}, extX:${character.mExtAnxiety}, awake:${character.mAwake}")

            //gate to prevent traversing unloaded internalAbsorbed tree
            if (HasInternalStimCondition.hasInternalStim(character))
                character.tree.step()
            else
                character.update(character.actionMoment + 0.01f)

            println("")
        }
    }

    @Test
    fun testLoadInternalAbsorbed() {
        initRoot()
        initNotAbsorbedSubtree()
        initInternalAbsorbedSubtree()

        (0..10).forEach { idx ->
            when (idx) {
                0 -> { character.mIntAnxiety = 0f; character.mExtAnxiety = 0f; character.hasRecognition = false }
                1 -> { character.mIntAnxiety = 0.1f; character.mExtAnxiety = 0.1f }
                2 -> { character.mIntAnxiety = 0.19f; character.mExtAnxiety = 0.2f }
                3 -> { character.mIntAnxiety = 0.2f; character.mExtAnxiety = 0.2f }
                4 -> { character.mIntAnxiety = 0.3f; character.mExtAnxiety = 0.29f }
                5 -> { character.mIntAnxiety = 0.3f; character.mExtAnxiety = 0.3f }
                6 -> { character.mIntAnxiety = 0.4f; character.mExtAnxiety = 0.4f }
                7 -> { character.hasRecognition = true }
                8 -> { character.mIntAnxiety = 0.3f; character.mExtAnxiety = 0.29f }
                9 -> { character.mIntAnxiety = 0.19f; character.mExtAnxiety = 0.2f }
                10 -> { character.mIntAnxiety = 0.1f; character.mExtAnxiety = 0.1f }
            }
            println("(character measures) : intX:${character.mIntAnxiety}, extX:${character.mExtAnxiety}, awake:${character.mAwake}")

            //gate to prevent traversing unloaded externalAbsorbed tree
            if (HasExternalStimCondition.hasExternalStim(character))
                character.tree.step()
            else
                character.update(character.actionMoment + 0.01f)

            println("")
        }
    }

    @Test
    fun testLoadExternalAbsorbed() {
        initRoot()
        initNotAbsorbedSubtree()
        initInternalAbsorbedSubtree()
        initExternalAbsorbedSubtree()

        (0..10).forEach { idx ->
            when (idx) {
                0 -> { character.mIntAnxiety = 0f; character.mExtAnxiety = 0f; character.hasRecognition = false; character.isOther = false }
                1 -> { character.mIntAnxiety = 0.1f; character.mExtAnxiety = 0.1f }
                2 -> { character.mIntAnxiety = 0.19f; character.mExtAnxiety = 0.2f }
                3 -> { character.mIntAnxiety = 0.2f; character.mExtAnxiety = 0.2f }
                4 -> { character.mIntAnxiety = 0.3f; character.mExtAnxiety = 0.29f }
                5 -> { character.mIntAnxiety = 0.3f; character.mExtAnxiety = 0.3f }
                6 -> { character.mIntAnxiety = 0.4f; character.mExtAnxiety = 0.4f }
                7 -> { character.mIntAnxiety = 0.4f; character.mExtAnxiety = 0.5f; character.isOther = true }
                8 -> { character.mIntAnxiety = 0.3f; character.mExtAnxiety = 0.32f; character.hasRecognition = true }
                9 -> { character.mIntAnxiety = 0.19f; character.mExtAnxiety = 0.2f }
                10 -> { character.mIntAnxiety = 0.1f; character.mExtAnxiety = 0.1f }
            }
            println("(character measures) : intX:${character.mIntAnxiety}, extX:${character.mExtAnxiety}, awake:${character.mAwake}")
            //gate to prevent traversing unloaded encounterAbsorbed tree
            if (HasRecognitionCondition.hasRecognition(character))
                character.tree.step()
            else
                character.update(character.actionMoment + 0.01f)
            println("")
        }
    }*/
}