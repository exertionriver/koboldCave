package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class WanderTask : ExecLeafTask() {

    override fun taskEnum() = TaskEnum.Wander

    override fun executeTask() {
        TaskEnum.Wander.updateObject(this.`object`)
        Gdx.app.log("${`object`::class.simpleName}", "${this::class.simpleName} ${`object`.name}(${`object`.aIntelligence}) wanders around..")
    }
}