package org.river.exertion.btree.v0_1.task_cond;

import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskType

class ResolveVictoryTask : ExecLeafTask() {

    override fun taskType() = TaskType.RESOLVE

    override fun executeTask() {
        taskType().updateObject(this.`object`)
        statusUpdate("${taskType().description()} as victory..")
    }
}