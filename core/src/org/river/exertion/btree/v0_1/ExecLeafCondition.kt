package org.river.exertion.btree.v0_1;

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task

abstract class ExecLeafCondition : LeafTask<IBTCharacter>() {

    override fun execute(): Status {
        return checkCondition()
    }

    abstract fun checkCondition() : Status

    override fun copyTo(task: Task<IBTCharacter>?): Task<IBTCharacter> {
        return task!!
    }
}