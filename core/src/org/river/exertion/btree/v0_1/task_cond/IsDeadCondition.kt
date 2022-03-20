package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafCondition

class IsDeadCondition : ExecLeafCondition() {

    fun isDead() = `object`.mLife < 0f

    override fun checkCondition() : Status {
        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${isDead()}")
        return if (isDead()) Status.SUCCEEDED else Status.FAILED
    }
}