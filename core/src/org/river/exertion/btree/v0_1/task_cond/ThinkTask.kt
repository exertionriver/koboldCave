package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class ThinkTask : ExecLeafTask() {

    override fun taskEnum() = TaskEnum.Think

    override fun executeTask() {
        TaskEnum.Think.updateObject(this.`object`)
        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${`object`.name}(${`object`.aIntelligence}) thinks things over..")
    }
}