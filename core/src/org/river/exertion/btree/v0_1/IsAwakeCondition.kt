package org.river.exertion.btree.v0_1;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute

class IsAwakeCondition : LeafTask<KoboldCharacter>() {

    @JvmField
    @TaskAttribute
    var howAwake : Float = 0f

    fun isAwake(howAwake : Float) = `object`.mAwake < howAwake

    override fun execute(): Status {

        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${isAwake(howAwake)}")

        return if (isAwake(howAwake)) Status.SUCCEEDED else Status.FAILED
    }

    override fun copyTo(task: Task<KoboldCharacter>?): Task<KoboldCharacter> {
        return task!!
    }
}