package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class SitTask : ExecLeafTask() {

    override fun taskEnum() = TaskEnum.Sit

    override fun executeTask() {
        taskEnum().updateObject(this.`object`)
        statusUpdate("sits..")
    }

}