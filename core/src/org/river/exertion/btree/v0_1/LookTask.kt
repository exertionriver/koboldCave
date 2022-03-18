package org.river.exertion.btree.v0_1;

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

class LookTask : ExecLeafTask() {

    override fun executeTask() {
        `object`.mIntAnxiety += 0.03f
        `object`.mExtAnxiety -= 0.03f
        Gdx.app.log("${`object`::class.simpleName}", "${this::class.simpleName} ${`object`.name}(${`object`.aIntelligence}) looks..")
    }
}