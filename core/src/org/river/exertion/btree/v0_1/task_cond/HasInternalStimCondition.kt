package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafCondition

class HasInternalStimCondition : ExecLeafCondition() {

    fun hasInternalStim() = `object`.mIntAnxiety > .2

    override fun checkCondition(): Status {
        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${hasInternalStim()}")
        return if (hasInternalStim()) Status.SUCCEEDED else Status.FAILED
    }
}