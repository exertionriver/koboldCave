package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class ScreechTask : ExecLeafTask() {

    override fun taskEnum() = TaskEnum.Screech

    override fun executeTask() {
        TaskEnum.Screech.updateObject(this.`object`)
        Gdx.app.log("${`object`::class.simpleName}", "${this::class.simpleName} ${`object`.name}(${`object`.aIntelligence}) screeches..!")
    }
}