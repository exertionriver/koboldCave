package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import org.river.exertion.btree.v0_1.ExecLeafCondition
import org.river.exertion.btree.v0_1.IBTCharacter
import org.river.exertion.btree.v0_1.TaskEnum

class IsStandingCondition : ExecLeafCondition() {

    override fun checkCondition() : Status {
        statusUpdate("${isStanding(`object`)}")
        return if (isStanding(`object`)) Status.SUCCEEDED else Status.FAILED
    }

    companion object {
        fun isStanding(character : IBTCharacter) : Boolean = character.isStanding

        fun isStandingSince(character : IBTCharacter) : Boolean =
                if (character.actionCountAgo(TaskEnum.Stand, character.momentsLongAgo * character.actionMoment) == 0) character.isStanding
                else false
    }
}