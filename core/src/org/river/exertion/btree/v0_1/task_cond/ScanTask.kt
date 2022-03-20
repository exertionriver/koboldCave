package org.river.exertion.btree.v0_1.task_cond;

import com.badlogic.gdx.Gdx
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.TaskEnum

class ScanTask : ExecLeafTask() {

    override fun taskEnum() = TaskEnum.Scan

    override fun executeTask() {
        TaskEnum.Scan.updateObject(this.`object`)
        Gdx.app.debug("${`object`::class.simpleName}", "${this::class.simpleName} ${`object`.name} scans..")
    }
}