package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafCondition

class HasRecognitionCondition : ExecLeafCondition() {

    override fun checkCondition(): Status {
        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${`object`.hasRecognition}")
        return if (`object`.hasRecognition) Status.SUCCEEDED else Status.FAILED
    }
}