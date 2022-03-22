package org.river.exertion.btree.v0_1.task_cond;

import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class ResolveVictoryTask : ExecLeafTask() {

    override fun taskEnum() = TaskEnum.Resolve

    override fun executeTask() {
        taskEnum().updateObject(this.`object`)
        statusUpdate("resolves as victory..!")
    }
}