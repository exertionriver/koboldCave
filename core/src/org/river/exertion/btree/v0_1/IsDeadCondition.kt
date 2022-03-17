package org.river.exertion.btree.v0_1;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

class IsDeadCondition : LeafTask<KoboldCharacter>() {

//    @JvmField
//    @TaskAttribute
//    var doing : String? = null

    fun isDead() = `object`.mLife < 0f

    override fun execute(): Status {

        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${isDead()}")

        return if (isDead()) Status.SUCCEEDED else Status.FAILED
    }

    override fun copyTo(task: Task<KoboldCharacter>?): Task<KoboldCharacter> {
        return task!!
    }
}