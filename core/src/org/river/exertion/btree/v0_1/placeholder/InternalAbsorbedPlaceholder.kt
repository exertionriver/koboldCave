package org.river.exertion.btree.v0_1.placeholder;

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import org.river.exertion.btree.v0_1.IBTCharacter

class InternalAbsorbedPlaceholder : LeafTask<IBTCharacter>() {

    override fun execute(): Status {
        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<IBTCharacter>?): Task<IBTCharacter> {
        return task!!
    }
}