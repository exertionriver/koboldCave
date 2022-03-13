package org.river.exertion.btree

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task

class BoredCondition : LeafTask<KoboldCharacter>() {

    override fun execute(): Status {

        val description = `object`.description

        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<KoboldCharacter>?): Task<KoboldCharacter> {
        return task!!
    }
}