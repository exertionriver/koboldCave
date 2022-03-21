package btree

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.decorator.Include
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser
import com.badlogic.gdx.ai.msg.Telegraph
import org.junit.jupiter.api.Test
import org.river.exertion.btree.v0_1.IBTCharacter
import org.river.exertion.btree.v0_1.KoboldCharacter
import org.river.exertion.btree.v0_1.NoneCharacter
import org.river.exertion.btree.v0_1.task_cond.HasRecognitionCondition
import org.river.exertion.geom.lattice.ArrayLattice
import org.river.exertion.geom.lattice.RoundedLattice
import java.io.FileReader
import java.nio.file.Paths


@ExperimentalUnsignedTypes
class TestBtreeLoads {

    var character = NoneCharacter()

    val rootLocation = "../android/assets/btree/entity/entity_v0_1_root.btree"
    val notAbsorbedSubtreeLocation = "../android/assets/btree/entity/entity_v0_1_notAbsorbed.btree"

    private fun initRoot() {
        val reader = FileReader(rootLocation)
        val parser = BehaviorTreeParser<Telegraph>(BehaviorTreeParser.DEBUG_HIGH)
        character.tree = parser.parse(reader, character) as BehaviorTree<IBTCharacter>
    }

    private fun initNotAbsorbedSubtree() {
        character.tree.getChild(0).getChild(0).getChild(0).addChild(Include<IBTCharacter?>().apply {
            this.subtree = notAbsorbedSubtreeLocation; this.lazy = true
        })
    }

    @Test
    fun testLoadRoot() {
        initRoot()
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
            character.tree.step()
            println("")
        }
    }

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
            character.tree.step()
            character.update(character.actionMoment)
            println("")
        }
    }
}