package org.river.exertion.btree.v0_1.task_cond;

import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskType

class SleepTask : ExecLeafTask() {

    override fun taskType() = TaskType.SLEEP

    override fun executeTask() {
        taskType().updateObject(this.`object`)
        statusUpdate(taskType().description())
    }

}