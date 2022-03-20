package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import org.river.exertion.btree.v0_1.ExecLeafCondition

class IsAwakeCondition : ExecLeafCondition() {

    @JvmField
    @TaskAttribute
    var minAwake : Float? = null //inclusive

    @JvmField
    @TaskAttribute
    var maxAwake : Float? = null //inclusive


    fun isAwake() : Boolean {
        return when {
            (minAwake != null) && (maxAwake != null) -> (`object`.mAwake >= minAwake!!) && (`object`.mAwake <= maxAwake!!)
            (minAwake != null) -> (`object`.mAwake >= minAwake!!)
            (maxAwake != null) -> (`object`.mAwake <= maxAwake!!)
            else -> false
        }
    }

    override fun checkCondition(): Status {
        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${isAwake()}")
        return if (isAwake()) Status.SUCCEEDED else Status.FAILED
    }
}