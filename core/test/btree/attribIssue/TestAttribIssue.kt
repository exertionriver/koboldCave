package btree.attribIssue

import com.badlogic.gdx.ai.btree.decorator.Include
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser
import org.junit.jupiter.api.Test
import org.river.exertion.btree.v0_1.IBTCharacter
import org.river.exertion.btree.v0_1.NoneCharacter
import java.io.FileReader


@ExperimentalUnsignedTypes
class TestAttribIssue {

    //these tests illustrate issue with gdx-ai subtree-include attributes as recorded here:
    //https://github.com/libgdx/gdx-ai/issues/121

    var character = NoneCharacter()

    val fullRootPath = "test/btree/attribIssue/full_root.btree"

    val fragRootPath = "test/btree/attribIssue/frag_root.btree"
    val fragSubtreePath = "test/btree/attribIssue/frag_subtree.btree"

    fun fragRootLocation() = character.tree.getChild(0)

    private fun initFullRoot() {
        val reader = FileReader(fullRootPath)
        val parser = BehaviorTreeParser<IBTCharacter>(BehaviorTreeParser.DEBUG_HIGH)
        character.tree = parser.parse(reader, character)
    }

    private fun initFragRoot() {
        val reader = FileReader(fragRootPath)
        val parser = BehaviorTreeParser<IBTCharacter>(BehaviorTreeParser.DEBUG_HIGH)
        character.tree = parser.parse(reader, character)
    }

    private fun initFragSubtree() {
        fragRootLocation().addChild(Include<IBTCharacter?>().apply {
            this.subtree = fragSubtreePath; this.lazy = true
        })
    }

    @Test
    fun testFullRoot() {
        initFullRoot()
        character.tree.step()
    }

    @Test
    fun testIncludeSubtree() {
        initFragRoot()
        initFragSubtree()
        character.tree.step()
    }
}