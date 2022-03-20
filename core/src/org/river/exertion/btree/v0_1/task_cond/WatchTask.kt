package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class WatchTask : ExecLeafTask() {

    override fun taskEnum() = TaskEnum.Watch

    override fun executeTask() {
        TaskEnum.Watch.updateObject(this.`object`)
        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${`object`.name}(${`object`.aWisdom}) watches..")
    }
}