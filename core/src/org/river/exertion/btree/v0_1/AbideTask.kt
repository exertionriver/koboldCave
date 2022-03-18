package org.river.exertion.btree.v0_1;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import org.river.exertion.btree.v0_1.ExecLeafTask.Companion.updateObject

class AbideTask : ExecLeafTask() {

    override fun executeTask() {
        updateObject(`object`)
        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${`object`.name} abides..")
    }

}