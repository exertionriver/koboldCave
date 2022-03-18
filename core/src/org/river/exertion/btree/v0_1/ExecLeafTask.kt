package org.river.exertion.btree.v0_1;

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task

open class ExecLeafTask : LeafTask<IBTCharacter>() {

    override fun execute(): Status {
        `object`.considerList.add(this)
        return Status.SUCCEEDED
    }

    open fun executeTask() {}

    override fun copyTo(task: Task<IBTCharacter>?): Task<IBTCharacter> {
        return task!!
    }

    companion object {
        fun AbideTask.updateObject(ibtCharacter: IBTCharacter) { ibtCharacter.mIntAnxiety += 0.01f }
        fun LookTask.updateObject(ibtCharacter: IBTCharacter) { ibtCharacter.mIntAnxiety += 0.01f }
    }
}