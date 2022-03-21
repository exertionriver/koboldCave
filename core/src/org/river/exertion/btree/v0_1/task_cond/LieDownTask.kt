package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class LieDownTask : ExecLeafTask() {

    override fun taskEnum() = TaskEnum.LieDown

    override fun executeTask() {
        TaskEnum.LieDown.updateObject(this.`object`)
        statusUpdate("lies down..")
    }

}