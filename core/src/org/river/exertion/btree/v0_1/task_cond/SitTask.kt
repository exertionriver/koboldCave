package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class SitTask : ExecLeafTask() {

    @JvmField
    @TaskAttribute
    var down : Boolean? = false

    @JvmField
    @TaskAttribute
    var up : Boolean? = false

    fun upOrDown() = if (down != null) "down" else if (up != null) "up" else "null"

    override fun taskEnum() = TaskEnum.Sit

    override fun executeTask() {
        TaskEnum.Sit.updateObject(this.`object`)
        statusUpdate("sits ${upOrDown()}..")
    }

}