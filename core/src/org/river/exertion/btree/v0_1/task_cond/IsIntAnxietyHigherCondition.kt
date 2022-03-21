package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafCondition

class IsIntAnxietyHigherCondition : ExecLeafCondition() {

    fun isIntHigher() = `object`.mIntAnxiety > `object`.mExtAnxiety

    override fun checkCondition(): Status {
        statusUpdate("${isIntHigher()}")
        return if ( isIntHigher() ) Status.SUCCEEDED else Status.FAILED
    }
}