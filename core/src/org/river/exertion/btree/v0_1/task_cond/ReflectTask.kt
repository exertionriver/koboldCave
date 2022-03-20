package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class ReflectTask : ExecLeafTask() {

    override fun taskEnum() = TaskEnum.Reflect

    override fun executeTask() {
        TaskEnum.Reflect.updateObject(this.`object`)
        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${`object`.name}(${`object`.aWisdom}) reflects..")
    }
}