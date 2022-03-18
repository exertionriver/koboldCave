package org.river.exertion.btree.v0_1

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.msg.Telegraph

interface IBTCharacter : Telegraph {

    var name : String
    var tree : BehaviorTree<IBTCharacter>

    //complementAbsorbed (neither)
    var mLife : Float
    var mLifeRegen : Float

    //internalAbsorbed
    var mIntAnxiety : Float
    var mIntAnxietyRegen : Float
    var mAwake : Float
    var mAwakeRegen : Float
    var aIntelligence : Float
    var aWisdom : Float

    //externalAbsorbed
    var mExtAnxiety : Float
    var mExtAnxietyRegen : Float
    var hasRecognition : Boolean
    var isOther : Boolean

    var decideMap : MutableMap<MutableList<LeafTask<IBTCharacter>>, Int>
    var considerList : MutableList<LeafTask<IBTCharacter>>

    var currentAction : LeafTask<IBTCharacter>
    var currentDecision : MutableList<LeafTask<IBTCharacter>>

    var considerTimer : Float
    val considerMoment : Float
    var actionTimer : Float
    val actionMoment : Float
}