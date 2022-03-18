package org.river.exertion.btree.v0_1

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import java.io.FileReader
import java.util.*

class KoboldCharacter : IBTCharacter {

    override var name = "razza" + Random().nextInt()
    override lateinit var tree : BehaviorTree<IBTCharacter>

    init {
        val reader = FileReader("android/assets/btree/entity_v0_1.btree")
        val parser = BehaviorTreeParser<IBTCharacter>(BehaviorTreeParser.DEBUG_NONE)
        tree = parser.parse(reader, this)
    }

    //complementAbsorbed (neither)
    override var mLife = 1f
    override var mLifeRegen = 0.05f

    //internalAbsorbed
    override var mIntAnxiety = .2f
    override var mIntAnxietyRegen = -0.01f
    override var mAwake = .6f
    override var mAwakeRegen = 0.05f
    override var aIntelligence = 8f
    override var aWisdom = 8f

    //externalAbsorbed
    override var mExtAnxiety = .2f
    override var mExtAnxietyRegen = -0.01f
    override var hasRecognition = false
    override var isOther = false //perceives an other

    var description = "toothy kobold!"

    override var decideMap = mutableMapOf<MutableList<LeafTask<IBTCharacter>>, Int>()
    override var considerList = mutableListOf<LeafTask<IBTCharacter>>()

    override var currentAction : LeafTask<IBTCharacter> = AbideTask()
    override var currentDecision = mutableListOf<LeafTask<IBTCharacter>>()

    override var considerTimer = 0f
    override val considerMoment = .06f
    override var actionTimer = 0f
    override val actionMoment = .6f

    override fun handleMessage(msg: Telegram?): Boolean {
        return true
    }

}