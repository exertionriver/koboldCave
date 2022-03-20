package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafCondition

class IsIntAnxietyHigherCondition : ExecLeafCondition() {

    fun isIntHigher() = `object`.mIntAnxiety > `object`.mExtAnxiety

    override fun checkCondition(): Status {
        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${isIntHigher()}")
        return if ( isIntHigher() ) Status.SUCCEEDED else Status.FAILED
    }
}