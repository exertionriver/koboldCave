package org.river.exertion.btree.v0_1.task_cond;

import org.river.exertion.btree.v0_1.ExecLeafCondition
import org.river.exertion.btree.v0_1.IBTCharacter
import org.river.exertion.btree.v0_1.TaskType

class IsStandingCondition : ExecLeafCondition() {

    override fun checkCondition() : Status {
        statusUpdate("${isStanding(`object`)}")
        return if (isStanding(`object`)) Status.SUCCEEDED else Status.FAILED
    }

    companion object {
        fun isStanding(character : IBTCharacter) : Boolean = character.isStanding

        fun isStandingSince(character : IBTCharacter) : Boolean =
                if (character.actionCountAgo(TaskType.STAND, character.momentsLongAgo * character.actionMoment) == 0) character.isStanding
                else false
    }
}