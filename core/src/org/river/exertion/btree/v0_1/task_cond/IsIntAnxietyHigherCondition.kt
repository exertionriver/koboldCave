package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafCondition
import org.river.exertion.btree.v0_1.IBTCharacter

class IsIntAnxietyHigherCondition : ExecLeafCondition() {

    override fun checkCondition(): Status {
        statusUpdate("${isIntHigher(`object`)}")
        return if ( isIntHigher(`object`) ) Status.SUCCEEDED else Status.FAILED
    }

    companion object {
        fun isIntHigher(character : IBTCharacter) = true // placeholder for polling external manifest
    }
}