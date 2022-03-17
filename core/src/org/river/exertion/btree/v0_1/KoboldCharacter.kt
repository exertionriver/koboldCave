package org.river.exertion.btree.v0_1

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import java.io.FileReader
import java.util.*

class KoboldCharacter : Telegraph {

    var name = "razza" + Random().nextInt()

    var tree : BehaviorTree<KoboldCharacter>

    init {
        val reader = FileReader("android/assets/btree/entity_v0_1.btree")
        val parser = BehaviorTreeParser<KoboldCharacter>(BehaviorTreeParser.DEBUG_NONE)
        tree = parser.parse(reader, this)
    }

    //complementAbsorbed (neither)
    var mLife = 1f
    var mLifeRegen = 0.05f

    //internalAbsorbed
    var mIntAnxiety = .2f
    var mIntAnxietyRegen = -0.01f
    var mAwake = .6f
    var mAwakeRegen = 0.05f
    var aIntelligence = 8f
    var aWisdom = 8f

    //externalAbsorbed
    var mExtAnxiety = .2f
    var mExtAnxietyRegen = -0.01f
    var hasRecognition = false
    var isOther = false //perceives an other

    var description = "toothy kobold!"

    override fun handleMessage(msg: Telegram?): Boolean {
        return true
    }

}