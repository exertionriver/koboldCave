package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import org.river.exertion.btree.v0_1.ExecLeafCondition
import org.river.exertion.btree.v0_1.IBTCharacter
import org.river.exertion.btree.v0_1.TaskEnum

class IsLyingDownCondition : ExecLeafCondition() {

    override fun checkCondition() : Status {
        statusUpdate("${isLyingDown(`object`)}")
        return if (isLyingDown(`object`)) Status.SUCCEEDED else Status.FAILED
    }

    companion object {
        fun isLyingDown(character : IBTCharacter) : Boolean = character.isLyingDown

        fun isLyingDownSince(character : IBTCharacter) : Boolean =
                if (character.actionCountAgo(TaskEnum.LieDown, character.momentsLongAgo * character.actionMoment) == 0) character.isLyingDown
                else false
    }
}