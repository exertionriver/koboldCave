package org.river.exertion.btree.v0_1.task_cond;

import org.river.exertion.btree.v0_1.ExecLeafCondition
import org.river.exertion.btree.v0_1.IBTCharacter
import org.river.exertion.btree.v0_1.Behavior

class IsSittingCondition : ExecLeafCondition() {

    override fun checkCondition() : Status {
        statusUpdate("${isSitting(`object`)}")
        return if (isSitting(`object`)) Status.SUCCEEDED else Status.FAILED
    }

    companion object {
        fun isSitting(character : IBTCharacter) : Boolean = character.isSitting

        fun isSittingSince(character : IBTCharacter) : Boolean =
                if (character.actionCountAgo(Behavior.SIT, character.momentsLongAgo * character.actionMoment) == 0) character.isSitting
                else false
    }
}