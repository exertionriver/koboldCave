package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import org.river.exertion.btree.v0_1.ExecLeafCondition
import org.river.exertion.btree.v0_1.IBTCharacter
import org.river.exertion.btree.v0_1.TaskEnum

class IsSittingCondition : ExecLeafCondition() {

    override fun checkCondition() : Status {
        statusUpdate("${isSitting(`object`)}")
        return if (isSitting(`object`)) Status.SUCCEEDED else Status.FAILED
    }

    companion object {
        fun isSitting(character : IBTCharacter) : Boolean = character.isSitting

        fun isSittingSince(character : IBTCharacter) : Boolean =
                if (character.actionCountAgo(TaskEnum.Sit, character.momentsLongAgo * character.actionMoment) == 0) character.isSitting
                else false
    }
}