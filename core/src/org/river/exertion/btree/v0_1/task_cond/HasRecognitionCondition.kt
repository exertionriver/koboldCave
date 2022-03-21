package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafCondition

class HasRecognitionCondition : ExecLeafCondition() {

    fun hasRecognition() = `object`.hasRecognition

    override fun checkCondition(): Status {
        statusUpdate("${hasRecognition()}")
        return if (`object`.hasRecognition) Status.SUCCEEDED else Status.FAILED
    }
}