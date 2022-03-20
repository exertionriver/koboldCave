package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafCondition

class IsOtherCondition : ExecLeafCondition() {

    override fun checkCondition(): Status {
        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${`object`.isOther}")
        return if (`object`.isOther) Status.SUCCEEDED else Status.FAILED
    }
}