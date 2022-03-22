package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class SleepTask : ExecLeafTask() {

    override fun taskEnum() = TaskEnum.Sleep

    override fun executeTask() {
        taskEnum().updateObject(this.`object`)
        statusUpdate("snoozes..")
    }

}