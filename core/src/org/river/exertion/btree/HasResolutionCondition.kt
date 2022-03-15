package org.river.exertion.btree;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

class HasResolutionCondition : LeafTask<KoboldCharacter>() {

//    @JvmField
//    @TaskAttribute
//    var doing : String? = null

    override fun execute(): Status {

        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${`object`.hasResolution}")

        return if (`object`.hasResolution) Status.SUCCEEDED else Status.FAILED
    }

    override fun copyTo(task: Task<KoboldCharacter>?): Task<KoboldCharacter> {
        return task!!
    }
}