package org.river.exertion.btree.v0_1;

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import org.river.exertion.btree.v0_1.task_cond.*

abstract class ExecLeafTask : LeafTask<IBTCharacter>() {

    override fun execute(): Status {
        `object`.decideSequenceList.add(this)
        return Status.SUCCEEDED
    }

    abstract fun executeTask()

    abstract fun taskEnum() : TaskEnum

    override fun copyTo(task: Task<IBTCharacter>?): Task<IBTCharacter> {
        return task!!
    }
}