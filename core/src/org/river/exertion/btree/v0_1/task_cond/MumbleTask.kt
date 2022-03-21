package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class MumbleTask : ExecLeafTask() {

    override fun taskEnum() = TaskEnum.Mumble

    override fun executeTask() {
        TaskEnum.Mumble.updateObject(this.`object`)
        statusUpdate("mumbles..")
    }
}