package btree

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.decorator.Include
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.math.Vector3
import org.junit.jupiter.api.Test
import org.river.exertion.MessageIds
import org.river.exertion.btree.v0_1.*
import org.river.exertion.btree.v0_1.task_cond.HasExternalStimCondition
import org.river.exertion.btree.v0_1.task_cond.HasInternalStimCondition
import org.river.exertion.btree.v0_1.task_cond.HasRecognitionCondition
import org.river.exertion.geom.lattice.ArrayLattice
import org.river.exertion.geom.lattice.RoundedLattice
import java.io.FileReader
import java.nio.file.Paths


@ExperimentalUnsignedTypes
class TestCManifest {

    var character = NoneCharacter()

    val rootLocation = "../android/assets/btree/entity/entity_v0_1_root.btree"
    val notAbsorbedSubtreePath = "../android/assets/btree/entity/entity_v0_1_notAbsorbed.btree"
    val internalAbsorbedSubtreePath = "../android/assets/btree/entity/entity_v0_1_internalAbsorbed.btree"
    val externalAbsorbedSubtreePath = "../android/assets/btree/entity/entity_v0_1_externalAbsorbed.btree"

    fun notAbsorbedSubtreeLocation() = character.tree.getChild(0).getChild(0).getChild(0)
    fun internalAbsorbedSubtreeLocation() = character.tree.getChild(0).getChild(1)
    fun externalAbsorbedSubtreeLocation() = character.tree.getChild(0).getChild(2)

    private fun initRoot() {
        val reader = FileReader(rootLocation)
        val parser = BehaviorTreeParser<IBTCharacter>(BehaviorTreeParser.DEBUG_NONE)
        character.tree = parser.parse(reader, character)
    }

    private fun initNotAbsorbedSubtree() {
        notAbsorbedSubtreeLocation().addChild(Include<IBTCharacter?>().apply {
            this.subtree = notAbsorbedSubtreePath; this.lazy = true
        })
    }

    private fun initInternalAbsorbedSubtree() {
        internalAbsorbedSubtreeLocation().addChild(Include<IBTCharacter?>().apply {
            this.subtree = internalAbsorbedSubtreePath; this.lazy = true
        })
    }

    private fun initExternalAbsorbedSubtree() {
        externalAbsorbedSubtreeLocation().addChild(Include<IBTCharacter?>().apply {
            this.subtree = externalAbsorbedSubtreePath; this.lazy = true
        })
    }

    @Test
    fun testSounds() {
        initRoot()
        initNotAbsorbedSubtree()
        initInternalAbsorbedSubtree()
        initExternalAbsorbedSubtree()

        val ordinarySound = ExternalPhenomenaInstance().apply {
            this.type = ExternalPhenomenaType.AUDITORY
            this.direction = 120f
            this.magnitude = 50f
            this.location = Vector3(10f, 10f, 10f)
            this.loss = .2f
        }
        MessageManager.getInstance().dispatchMessage(null, MessageIds.PHENOMENA.id(), ordinarySound)

        character.characterManifest.perceptionList.forEach { println(it) }
    }
}