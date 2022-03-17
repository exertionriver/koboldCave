package org.river.exertion.btree.v0_1;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

class ReflectTask : LeafTask<KoboldCharacter>() {

//    @JvmField
//    @TaskAttribute
//    var doing : String? = null

    override fun execute(): Status {

        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${`object`.name}(${`object`.aWisdom}) reflects..")

        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<KoboldCharacter>?): Task<KoboldCharacter> {
        return task!!
    }
}