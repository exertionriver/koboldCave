package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class AbideTask : ExecLeafTask() {

    override fun taskEnum() = TaskEnum.Abide

    override fun executeTask() {
        TaskEnum.Abide.updateObject(this.`object`)
        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${`object`.name} abides..")
    }
}