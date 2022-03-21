package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafCondition

class HasExternalStimCondition : ExecLeafCondition() {

    fun hasExternalStim() = `object`.mExtAnxiety > .3

    override fun checkCondition(): Status {
        statusUpdate("${hasExternalStim()}")
        return if (hasExternalStim()) Status.SUCCEEDED else Status.FAILED
    }
}