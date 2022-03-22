package btree.attribIssue;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import org.river.exertion.btree.v0_1.ExecLeafCondition
import org.river.exertion.btree.v0_1.IBTCharacter

class IsAwakeAttribCondition : ExecLeafCondition() {

    @JvmField
    @TaskAttribute
    var minAwake : Float = 0f //inclusive

    @JvmField
    @TaskAttribute
    var maxAwake : Float = 1f //inclusive

    override fun checkCondition(): Status {
        statusUpdate("${isAwake(`object`, minAwake, maxAwake)} ($minAwake, $maxAwake)")
        return if (isAwake(`object`, minAwake, maxAwake)) Status.SUCCEEDED else Status.FAILED
    }

    companion object {
        fun isAwake(character : IBTCharacter, minAwake : Float?, maxAwake : Float?) : Boolean {
            return when {
                (minAwake != null) && (maxAwake != null) -> (character.mAwake >= minAwake) && (character.mAwake <= maxAwake)
                (minAwake != null) -> (character.mAwake >= minAwake)
                (maxAwake != null) -> (character.mAwake <= maxAwake)
                else -> character.mAwake > 0
            }
        }
    }
}