package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafCondition
import org.river.exertion.btree.v0_1.IBTCharacter

class HasInternalStimCondition : ExecLeafCondition() {

    override fun checkCondition(): Status {
        statusUpdate("${hasInternalStim(`object`)}")
        return if (hasInternalStim(`object`)) Status.SUCCEEDED else Status.FAILED
    }

    companion object {
        fun hasInternalStim(character : IBTCharacter) = character.mIntAnxiety > .2
    }
}