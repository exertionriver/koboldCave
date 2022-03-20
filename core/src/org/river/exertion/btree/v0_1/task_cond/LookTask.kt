package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class LookTask : ExecLeafTask() {

    override fun taskEnum() = TaskEnum.Look

    override fun executeTask() {
        TaskEnum.Look.updateObject(this.`object`)
        Gdx.app.log("${`object`::class.simpleName}", "${this::class.simpleName} ${`object`.name}(${`object`.aIntelligence}) looks..")
    }
}