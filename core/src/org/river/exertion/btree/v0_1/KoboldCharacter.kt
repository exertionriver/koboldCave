package org.river.exertion.btree.v0_1

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.msg.Telegram
import org.river.exertion.btree.v0_1.task_cond.AbideTask
import java.util.*

class KoboldCharacter : IBTCharacter {

    override var name = "razza" + Random().nextInt()
    var description = "toothy kobold!"

    override lateinit var tree : BehaviorTree<IBTCharacter>


    init { init()
        addEncounterSubtree("android/assets/btree/kobold_encounter_v0_1.btree")
        addInternalAbsorbedInternalActionSubtree("android/assets/btree/kobold_iaias_v0_1.btree")
        addInternalAbsorbedExternalActionSubtree("android/assets/btree/kobold_iaeas_v0_1.btree")
    }

    //noneAbsorbed
    override var mLife = 1f
    override var mLifeRegen = 0.05f

    //internalAbsorbed
    override var mIntAnxiety = .2f
    override var mAwake = .6f
    override var aIntelligence = 8f
    override var aWisdom = 8f

    override var mTiredness = .2f
    override var mExhaustion = .2f
    override var mHunger = .2f
    override var mThirst = .2f

    //externalAbsorbed
    override var mExtAnxiety = .22f
    override var hasRecognition = true
    override var isOther = false //perceives an other

    override var isLyingDown = false
    override var isSitting = true
    override var isStandingUp = false

    override var decideSequenceList = mutableListOf<ExecLeafTask>()
    override var currentAction : ExecLeafTask = AbideTask()
    override var actionList = mutableListOf<Pair<TaskEnum, Float>>()

    override var actionTimer = 0f
    override val actionMoment = .6f

    override fun handleMessage(msg: Telegram?): Boolean {
        return true
    }

}