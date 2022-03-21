package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class StandUpTask : ExecLeafTask() {

    override fun taskEnum() = TaskEnum.StandUp

    override fun executeTask() {
        TaskEnum.StandUp.updateObject(this.`object`)
        statusUpdate("stands up..")
    }

}