package org.river.exertion.btree.v0_1;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task

abstract class ExecLeafTask : LeafTask<IBTCharacter>() {

    override fun execute(): Status {
        `object`.decideSequenceList.add(this)
        return Status.SUCCEEDED
    }

    abstract fun taskType() : TaskType

    abstract fun executeTask()

    fun statusUpdate(update : String) {
        if (Gdx.app != null)
            Gdx.app.debug("${this::class.simpleName}", "${`object`.noumenonInstance.instanceName} $update")
        else
            println("[${this::class.simpleName}] ${`object`.noumenonInstance.instanceName} $update")
    }

    override fun copyTo(task: Task<IBTCharacter>?): Task<IBTCharacter> {
        return task!!
    }
}