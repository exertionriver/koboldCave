package btree.attribIssue;

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import org.river.exertion.btree.v0_1.IBTCharacter

class SubtreePlaceholder : LeafTask<IBTCharacter>() {

    override fun execute(): Status {
        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<IBTCharacter>?): Task<IBTCharacter> {
        return task!!
    }
}