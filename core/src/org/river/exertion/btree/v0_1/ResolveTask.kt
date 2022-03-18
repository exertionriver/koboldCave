package org.river.exertion.btree.v0_1;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute

class ResolveTask : ExecLeafTask() {

    @JvmField
    @TaskAttribute
    var `as` : String? = null

    override fun executeTask() {
        `object`.mIntAnxiety -= 0.05f
        `object`.mExtAnxiety -= 0.05f
        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${`object`.name} resolves as $`as`..")
    }
}