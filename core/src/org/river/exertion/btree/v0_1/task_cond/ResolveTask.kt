package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class ResolveTask : ExecLeafTask() {

    @JvmField
    @TaskAttribute
    var asStatus : String = ""

    override fun taskEnum() = TaskEnum.Resolve

    override fun executeTask() {
        TaskEnum.Resolve.updateObject(this.`object`)
        statusUpdate("resolves as $asStatus..")
    }
}