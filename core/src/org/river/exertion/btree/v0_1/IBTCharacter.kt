package org.river.exertion.btree.v0_1

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.decorator.Include
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.MessageIds
import org.river.exertion.ai.manifest.CharacterManifest
import org.river.exertion.ai.memory.CharacterMemory
import org.river.exertion.ai.noumena.NoumenonInstance
import org.river.exertion.ai.phenomena.ExternalPhenomenaInstance
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance
import org.river.exertion.btree.v0_1.task_cond.HasRecognitionCondition
import java.io.FileReader

interface IBTCharacter : Telegraph {

    val noumenonInstance : NoumenonInstance

    var tree : BehaviorTree<IBTCharacter>

    var characterManifest : CharacterManifest
    var characterMemory : CharacterMemory

    fun rootLocation() = "../android/assets/btree/entity/entity_v0_1_root.btree"
    fun notAbsorbedSubtreePath() = "../android/assets/btree/entity/entity_v0_1_notAbsorbed.btree"
    fun internalAbsorbedSubtreePath() = "../android/assets/btree/entity/entity_v0_1_internalAbsorbed.btree"
    fun externalAbsorbedSubtreePath() = "../android/assets/btree/entity/entity_v0_1_externalAbsorbed.btree"

    fun notAbsorbedSubtreeLocation() = this.tree.getChild(0).getChild(0).getChild(0)
    fun internalAbsorbedSubtreeLocation() = this.tree.getChild(0).getChild(1)
    fun externalAbsorbedSubtreeLocation() = this.tree.getChild(0).getChild(2)

    fun initRoot() {
        val reader = FileReader(rootLocation())
        val parser = BehaviorTreeParser<IBTCharacter>(BehaviorTreeParser.DEBUG_HIGH)
        this.tree = parser.parse(reader, this)

        MessageManager.getInstance().addListener(this, MessageIds.EXT_PHENOMENA.id())
        MessageManager.getInstance().addListener(this, MessageIds.INT_PHENOMENA.id())
    }

    fun initNotAbsorbedSubtree() {
        notAbsorbedSubtreeLocation().addChild(Include<IBTCharacter?>().apply {
            this.subtree = notAbsorbedSubtreePath(); this.lazy = true
        })
    }

    fun initInternalAbsorbedSubtree() {
        internalAbsorbedSubtreeLocation().addChild(Include<IBTCharacter?>().apply {
            this.subtree = internalAbsorbedSubtreePath(); this.lazy = true
        })
    }

    fun initExternalAbsorbedSubtree() {
        externalAbsorbedSubtreeLocation().addChild(Include<IBTCharacter?>().apply {
            this.subtree = externalAbsorbedSubtreePath(); this.lazy = true
        })
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
    var hasRecognition : Boolean
    var isOther : Boolean

    var isLyingDown : Boolean
    var isSitting : Boolean
    var isStanding : Boolean

    var decideSequenceList : MutableList<ExecLeafTask> //sequence of actions
    var currentAction : ExecLeafTask
    var actionList : MutableList<Pair<TaskEnum, Float>> //actions already taken

    var actionTimer : Float
    val actionMoment : Float
    val momentsLongAgo : Float

    @Suppress("NewApi")
    fun update(delta : Float) {
        this.actionTimer += delta

        characterManifest.update(delta)
        characterMemory.update(delta, this)

        if (this.actionTimer > this.actionMoment) {

/*            if (Gdx.app != null)
                Gdx.app.log("character measures", "intX:${this.mIntAnxiety}, extX:${this.mExtAnxiety}, awake:${this.mAwake}")
            else
                println("(character measures) : intX:${this.mIntAnxiety}, extX:${this.mExtAnxiety}, awake:${this.mAwake}")
*/
            this.actionTimer -= this.actionTimer
            this.tree.step()

            if (Gdx.app != null)
                Gdx.app.debug("character current decision", "${this.decideSequenceList}")
            else
                println("(character current decision) ${this.decideSequenceList}")

            this.currentAction = this.decideSequenceList.first()
            this.decideSequenceList.removeFirst()//remove(character.decideSequenceList.first())

            if (Gdx.app != null)
                Gdx.app.log("character current action", "${this.noumenonInstance.instanceName}: ${this.currentAction}")
            else
                println("(character current action) ${this.noumenonInstance.instanceName}: ${this.currentAction}")

            val execTask = this.currentAction
            if (execTask is ExecLeafTask) execTask.executeTask()

            this.actionList.add(Pair(this.currentAction.taskEnum(), this.actionMoment))

            this.actionMap(momentsLongAgo * actionMoment).entries.sortedByDescending { it.value }.forEach {
                if (Gdx.app != null)
                    Gdx.app.debug("character actionMap($momentsLongAgo)", "${it.key}: (${it.value})")
                else
                    println("(character actionMap $momentsLongAgo) ${it.key}: (${it.value})")
            }
        }
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender != this) ) {
            if (msg.message == MessageIds.EXT_PHENOMENA.id()) {
                characterManifest.addImpression(msg.sender as IBTCharacter, (msg.extraInfo as ExternalPhenomenaInstance).impression())
            }
            if (msg.message == MessageIds.INT_PHENOMENA.id()) {
                characterManifest.addImpression((msg.extraInfo as InternalPhenomenaInstance).impression())
            }
        }
        return true
    }

}