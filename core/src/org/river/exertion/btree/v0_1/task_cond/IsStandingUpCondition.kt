package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import org.river.exertion.btree.v0_1.ExecLeafCondition
import org.river.exertion.btree.v0_1.TaskEnum

class IsStandingUpCondition : ExecLeafCondition() {

    @JvmField
    @TaskAttribute
    var minHowLong : Float? = null //inclusive

    fun isStandingUp() = `object`.isStandingUp

    fun isStandingUpMinHowLong() =
            if (minHowLong == null) isStandingUp()
            else if (`object`.actionCountAgo(TaskEnum.StandUp, minHowLong!!) == 0) isStandingUp()
            else false

    override fun checkCondition() : Status {
        statusUpdate("${isStandingUpMinHowLong()} ($minHowLong)")
        return if (isStandingUpMinHowLong()) Status.SUCCEEDED else Status.FAILED
    }
}