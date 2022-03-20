package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafCondition

class IsExtAnxietyHigherCondition : ExecLeafCondition() {

    fun isExtHigher() = `object`.mExtAnxiety > `object`.mIntAnxiety

    override fun checkCondition(): Status {
        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${isExtHigher()}")
        return if ( isExtHigher() ) Status.SUCCEEDED else Status.FAILED
    }
}