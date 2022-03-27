package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafCondition
import org.river.exertion.btree.v0_1.IBTCharacter

class IsExtAnxietyHigherCondition : ExecLeafCondition() {

    override fun checkCondition(): Status {
        statusUpdate("${isExtHigher(`object`)}")
        return if ( isExtHigher(`object`) ) Status.SUCCEEDED else Status.FAILED
    }

    companion object {
        fun isExtHigher(character : IBTCharacter) = false // placeholder for polling external manifest
    }
}