package org.river.exertion.btree.v0_1

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.decorator.Include
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.btree.v0_1.task_cond.HasRecognitionCondition
import java.io.FileReader

interface IBTCharacter : Telegraph {

    var name : String
    var tree : BehaviorTree<IBTCharacter>

    fun init() {
        val reader = FileReader("android/assets/btree/entity_v0_1.btree")
        val parser = BehaviorTreeParser<IBTCharacter>(BehaviorTreeParser.DEBUG_NONE)
        tree = parser.parse(reader, this)
    }

    //after init()
    fun addEncounterSubtree(subTreeLocation : String) {
        tree.getChild(0).addChild(Include<IBTCharacter?>().apply { this.subtree = subTreeLocation; this.lazy = true; this.guard = HasRecognitionCondition() })
    }

    fun addInternalAbsorbedInternalActionSubtree(subTreeLocation : String) {
        tree.getChild(0).getChild(1).getChild(1).getChild(1).getChild(0).addChild(Include<IBTCharacter?>().apply { this.subtree = subTreeLocation; this.lazy = true })
    }

    fun addInternalAbsorbedExternalActionSubtree(subTreeLocation : String) {
        tree.getChild(0).getChild(1).getChild(1).getChild(1).getChild(1).addChild(Include<IBTCharacter?>().apply { this.subtree = subTreeLocation; this.lazy = true })
    }

    fun actionMap(ago : Float) : MutableMap<TaskEnum, Int> {

        var agoCounter = 0f
        var agoIdx = 0
        val returnMap = mutableMapOf<TaskEnum, Int>()

        if ( actionList.isNotEmpty() ) {
            val agoActionList = actionList.reversed()

            while ( (agoCounter < ago) && (agoIdx < agoActionList.size) ) {
                val taskCount = returnMap[agoActionList[agoIdx].first]
                if (taskCount == null) {
                    returnMap[agoActionList[agoIdx].first] = 1
                } else {
                    returnMap[agoActionList[agoIdx].first] = taskCount + 1
                }
                agoCounter += agoActionList[agoIdx].second
                agoIdx++
            }
        }

        return returnMap
    }

    fun actionCountAgo(taskEnum: TaskEnum, ago: Float) : Int {

        var agoCounter = 0f
        var agoIdx = 0
        var count = 0

        if ( actionList.isNotEmpty() ) {
            val agoActionList = actionList.reversed()

            while ( (agoCounter < ago) && (agoIdx < agoActionList.size) ) {
                if ( agoActionList[agoIdx].first == taskEnum ) count++
                agoCounter += agoActionList[agoIdx].second
                agoIdx++
            }
        }

        return count
    }

    //complementAbsorbed (neither)
    var mLife : Float
    var mLifeRegen : Float

    //internalAbsorbed
    var mIntAnxiety : Float
    var mAwake : Float
    var aIntelligence : Float
    var aWisdom : Float

    var mTiredness : Float
    var mExhaustion : Float
    var mHunger : Float
    var mThirst : Float

    //externalAbsorbed
    var mExtAnxiety : Float
    var hasRecognition : Boolean
    var isOther : Boolean

    var isLyingDown : Boolean
    var isSitting : Boolean
    var isStandingUp : Boolean

    var decideSequenceList : MutableList<ExecLeafTask> //sequence of actions
    var currentAction : ExecLeafTask
    var actionList : MutableList<Pair<TaskEnum, Float>> //actions already taken

    var actionTimer : Float
    val actionMoment : Float

}