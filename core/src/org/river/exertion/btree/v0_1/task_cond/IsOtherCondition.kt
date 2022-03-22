package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafCondition
import org.river.exertion.btree.v0_1.IBTCharacter

class IsOtherCondition : ExecLeafCondition() {

    override fun checkCondition(): Status {
        statusUpdate("${isOther(`object`)}")
        return if (isOther(`object`)) Status.SUCCEEDED else Status.FAILED
    }

    companion object {
        fun isOther(character : IBTCharacter) = character.isOther
    }
}