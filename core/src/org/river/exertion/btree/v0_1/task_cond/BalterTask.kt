package org.river.exertion.btree.v0_1.task_cond;

import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskType

class BalterTask : ExecLeafTask() {

    override fun taskType() = TaskType.BALTER

    override fun executeTask() {
        taskType().updateObject(this.`object`)
        statusUpdate(taskType().description())
    }
}