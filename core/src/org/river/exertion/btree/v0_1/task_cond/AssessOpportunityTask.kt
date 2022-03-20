package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class AssessOpportunityTask : ExecLeafTask() {

    override fun taskEnum() = TaskEnum.AssessOpportunity

    override fun executeTask() {
        TaskEnum.AssessOpportunity.updateObject(this.`object`)
        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${`object`.name} assesses opportunity..")
    }
}