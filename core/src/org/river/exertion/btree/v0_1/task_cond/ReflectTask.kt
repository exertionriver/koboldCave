package org.river.exertion.btree.v0_1.task_cond;

import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskType

class ReflectTask : ExecLeafTask() {

    override fun taskType() = TaskType.REFLECT

    override fun executeTask() {
        taskType().updateObject(this.`object`)
        statusUpdate(taskType().description())
    }
}