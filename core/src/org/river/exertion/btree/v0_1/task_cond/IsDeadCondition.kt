package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafCondition
import org.river.exertion.btree.v0_1.IBTCharacter

class IsDeadCondition : ExecLeafCondition() {

    override fun checkCondition() : Status {
        statusUpdate("${isDead(`object`)}")
        return if (isDead(`object`)) Status.SUCCEEDED else Status.FAILED
    }

    companion object {
        fun isDead(character : IBTCharacter) = character.mLife < 0f
    }
}