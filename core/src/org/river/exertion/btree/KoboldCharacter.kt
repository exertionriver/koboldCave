package org.river.exertion.btree

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import java.io.FileReader


class KoboldCharacter : Telegraph {

    var tree : BehaviorTree<KoboldCharacter>

    init {
        val reader = FileReader("android/assets/btree/kobold.btree")
        val parser = BehaviorTreeParser<KoboldCharacter>(BehaviorTreeParser.DEBUG_HIGH)
        tree = parser.parse(reader, this)
    }
//    var tree = BehaviorTreeLibraryManager.getInstance().createBehaviorTree("btree/kobold.btree", this)
    var description = "toothy kobold!"

    override fun handleMessage(msg: Telegram?): Boolean {
        return true
    }

}