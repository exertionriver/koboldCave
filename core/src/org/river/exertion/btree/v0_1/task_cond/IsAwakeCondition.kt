package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import org.river.exertion.btree.v0_1.ExecLeafCondition
import org.river.exertion.btree.v0_1.IBTCharacter

class IsAwakeCondition : ExecLeafCondition() {

    override fun checkCondition(): Status {
        statusUpdate("${isAwake(`object`)}")
        return if (isAwake(`object`)) Status.SUCCEEDED else Status.FAILED
    }

    companion object {
        fun isAwake(character : IBTCharacter) = character.mAwake > 0
    }
}