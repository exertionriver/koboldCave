package org.river.exertion.btree.v0_1.task_cond;

import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.Behavior

class ResolveDrawTask : ExecLeafTask() {

    override fun taskType() = Behavior.RESOLVE

    override fun executeTask() {
        taskType().updateObject(this.`object`)
        statusUpdate("${taskType().description()} as draw..")
    }
}