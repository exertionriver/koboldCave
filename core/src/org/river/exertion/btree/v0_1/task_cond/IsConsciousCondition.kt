package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import org.river.exertion.btree.v0_1.ExecLeafCondition
import org.river.exertion.btree.v0_1.IBTCharacter

class IsConsciousCondition : ExecLeafCondition() {

    override fun checkCondition(): Status {
        statusUpdate("${isConscious(`object`)}")
        return if (isConscious(`object`)) Status.SUCCEEDED else Status.FAILED
    }

    companion object {
        fun isConscious(character : IBTCharacter) = character.mAwake >= .2f
    }
}