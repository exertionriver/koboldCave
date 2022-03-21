package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class ReflectTask : ExecLeafTask() {

    override fun taskEnum() = TaskEnum.Reflect

    override fun executeTask() {
        TaskEnum.Reflect.updateObject(this.`object`)
        statusUpdate("reflects upon some things..")
    }
}