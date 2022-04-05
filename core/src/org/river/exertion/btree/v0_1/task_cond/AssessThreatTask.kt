package org.river.exertion.btree.v0_1.task_cond;

import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskType

class AssessThreatTask : ExecLeafTask() {

    override fun taskType() = TaskType.ASSESS_THREAT

    override fun executeTask() {
        taskType().updateObject(this.`object`)
        statusUpdate(taskType().description())
    }
}