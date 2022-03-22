package org.river.exertion.btree.v0_1.task_cond;

import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class StandTask : ExecLeafTask() {

    override fun taskEnum() = TaskEnum.Stand

    override fun executeTask() {
        taskEnum().updateObject(this.`object`)
        statusUpdate("stands..")
    }

}