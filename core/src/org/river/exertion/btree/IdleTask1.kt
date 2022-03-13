package org.river.exertion.btree;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

class IdleTask1 : LeafTask<KoboldCharacter>() {

    @JvmField
    @TaskAttribute
    var doing : String? = null

    override fun execute(): Status {

        val description = `object`.description

        println("$description kobold doing ${doing}")

        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<KoboldCharacter>?): Task<KoboldCharacter> {
        return task!!
    }
}