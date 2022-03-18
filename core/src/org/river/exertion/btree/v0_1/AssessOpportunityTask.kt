package org.river.exertion.btree.v0_1;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

class AssessOpportunityTask : ExecLeafTask() {

    override fun executeTask() {
        `object`.mIntAnxiety += 0.05f
        `object`.mExtAnxiety += 0.05f
        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${`object`.name} assesses opportunity..")
    }
}