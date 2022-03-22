package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafCondition
import org.river.exertion.btree.v0_1.IBTCharacter

class HasRecognitionCondition : ExecLeafCondition() {

    override fun checkCondition(): Status {
        statusUpdate("${hasRecognition(`object`)}")
        return if (hasRecognition(`object`)) Status.SUCCEEDED else Status.FAILED
    }

    companion object {
        fun hasRecognition(character : IBTCharacter) = character.hasRecognition
    }
}