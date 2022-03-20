package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class BalterTask : ExecLeafTask() {

    override fun taskEnum() = TaskEnum.Balter

    override fun executeTask() {
        TaskEnum.Balter.updateObject(this.`object`)
        Gdx.app.log("${`object`::class.simpleName}", "${this::class.simpleName} ${`object`.name}(${`object`.aIntelligence}) balters..!")
    }
}