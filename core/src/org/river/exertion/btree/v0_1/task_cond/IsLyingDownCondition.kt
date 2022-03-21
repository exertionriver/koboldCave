package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import org.river.exertion.btree.v0_1.ExecLeafCondition
import org.river.exertion.btree.v0_1.TaskEnum

class IsLyingDownCondition : ExecLeafCondition() {

    @JvmField
    @TaskAttribute
    var minHowLong : Float? = null //inclusive

    fun isLyingDown() = `object`.isLyingDown

    fun isLyingDownMinHowLong() =
            if (minHowLong == null) isLyingDown()
            else if (`object`.actionCountAgo(TaskEnum.LieDown, minHowLong!!) == 0) isLyingDown()
            else false

    override fun checkCondition() : Status {
        statusUpdate("${isLyingDownMinHowLong()} ($minHowLong)")
        return if (isLyingDownMinHowLong()) Status.SUCCEEDED else Status.FAILED
    }
}