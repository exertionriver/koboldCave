package org.river.exertion.btree.v0_1

import com.badlogic.gdx.ai.btree.BehaviorTree
import org.river.exertion.ai.manifest.CharacterManifest
import org.river.exertion.ai.memory.CharacterMemory
import org.river.exertion.ai.noumena.other.being.humanoid.low_race.KoboldNoumenon.kobold
import org.river.exertion.btree.v0_1.task_cond.AbideTask

class KoboldCharacter : IBTCharacter {

    override val noumenonInstance = kobold {}

    var description = "toothy kobold!"

    //defining characteristics of a kobold, if you perceive enough of these, you can identify it as a kobold
    //e.g. characteristic: 'child-sized' : .25 , 'scaley' : .25 , 'upright' : .25, 'has tail', 'dragon-faced' : .25 type-characteristic
    //other-characteristic: 'humanoid', 'articulate', 'super-animal intelligence', 'eyes in front' other-characteristic
    //group-characteristic: 'tunic with red hand' : .25
    //individual-characteristic:
    // experience adds up all these characteristic values and improves chances of identifying kobold type, etc.

    override var characterManifest = CharacterManifest()
    override var characterMemory = CharacterMemory()

    override lateinit var tree : BehaviorTree<IBTCharacter>

    init {
        initRoot()
        initNotAbsorbedSubtree()
        initInternalAbsorbedSubtree()
        initExternalAbsorbedSubtree()
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
    override var hasRecognition = true
    override var isOther = false //perceives an other

    override var isLyingDown = false
    override var isSitting = true
    override var isStanding = false

    override var decideSequenceList = mutableListOf<ExecLeafTask>()
    override var currentAction : ExecLeafTask = AbideTask()
    override var actionList = mutableListOf<Pair<Behavior, Float>>()

    override var actionTimer = 0f
    override val actionMoment = .6f
    override val momentsLongAgo = 10f


}