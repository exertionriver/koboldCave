package org.river.exertion.btree.v0_1;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import org.river.exertion.btree.v0_1.task_cond.*

abstract class ExecLeafTask : LeafTask<IBTCharacter>() {

    override fun execute(): Status {
        `object`.decideSequenceList.add(this)
        return Status.SUCCEEDED
    }

    abstract fun taskEnum() : TaskEnum

    abstract fun executeTask()

    fun statusUpdate(update : String) {
        if (Gdx.app != null)
            Gdx.app.debug("${this::class.simpleName}", "${`object`.signature.individual} $update")
        else
            println("[${this::class.simpleName}] ${`object`.signature.individual} $update")
    }

    override fun copyTo(task: Task<IBTCharacter>?): Task<IBTCharacter> {
        return task!!
    }
}