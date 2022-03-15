package org.river.exertion.btree

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import java.io.FileReader


class KoboldCharacter : Telegraph {

    var tree : BehaviorTree<KoboldCharacter>

    init {
        val reader = FileReader("android/assets/btree/entity.btree")
        val parser = BehaviorTreeParser<KoboldCharacter>(BehaviorTreeParser.DEBUG_NONE)
        tree = parser.parse(reader, this)
    }

    //complementAbsorbed (neither)
    var hasInternalStim = false
    var lifeforce = 1f
    fun isDead() = lifeforce < 0f
    var isSleeping = false

    //internalAbsorbed
    var hasExternalStim = false
    var isDreaming = false
    var isThinking = false

    //externalAbsorbed
    var hasRecognition = false
    var isOther = false //perceives an other
    var isThreat = false

    //encounterAbsorbed
    var hasResolution = false
    var isImproving = false
    var isWorsening = false

    var description = "toothy kobold!"

    override fun handleMessage(msg: Telegram?): Boolean {
        return true
    }

}