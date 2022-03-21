package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import org.river.exertion.btree.v0_1.ExecLeafCondition
import org.river.exertion.btree.v0_1.TaskEnum

class IsSittingCondition : ExecLeafCondition() {

    @JvmField
    @TaskAttribute
    var minHowLong : Float? = null //inclusive

    fun isSitting() = `object`.isSitting

    fun isSittingMinHowLong() =
        if (minHowLong == null) isSitting()
        else if (`object`.actionCountAgo(TaskEnum.Sit, minHowLong!!) == 0) isSitting()
        else false

    override fun checkCondition() : Status {
        statusUpdate("${isSittingMinHowLong()} ($minHowLong)")
        return if (isSittingMinHowLong()) Status.SUCCEEDED else Status.FAILED
    }
}