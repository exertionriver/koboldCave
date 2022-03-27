package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafCondition
import org.river.exertion.btree.v0_1.IBTCharacter

class HasExternalStimCondition : ExecLeafCondition() {

    override fun checkCondition(): Status {
        statusUpdate("${hasExternalStim(`object`)}")
        return if (hasExternalStim(`object`)) Status.SUCCEEDED else Status.FAILED
    }

    companion object {
        fun hasExternalStim(character : IBTCharacter) = character.mIntAnxiety > .5 //placeholder for polling external manifest
    }
}