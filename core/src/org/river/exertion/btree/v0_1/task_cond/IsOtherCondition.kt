package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafCondition

class IsOtherCondition : ExecLeafCondition() {

    fun isOther() = `object`.isOther

    override fun checkCondition(): Status {
        statusUpdate("${isOther()}")
        return if (`object`.isOther) Status.SUCCEEDED else Status.FAILED
    }
}