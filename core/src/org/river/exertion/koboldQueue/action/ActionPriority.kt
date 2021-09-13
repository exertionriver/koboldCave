package org.river.exertion.koboldQueue.action

class ActionPriority(val priority: String, val valueClass : Int, val valueInClass : Int ) : Comparable<ActionPriority> {

    companion object {
        val BaseAction = ActionPriority("baseAction", 0, 0) //attempts to run perpetually
        val HighFirst = ActionPriority("highFirst", 1, 1)
        val HighSecond = ActionPriority("highSecond", 1, 2)
        val HighThird = ActionPriority("highThird", 1, 3)
        val MediumFirst = ActionPriority("mediumFirst", 2, 1)
        val MediumSecond = ActionPriority("mediumSecond", 2, 2)
        val MediumThird = ActionPriority("mediumThird", 2, 3)
        val LowFirst = ActionPriority("lowFirst", 3, 1)
        val LowSecond = ActionPriority("lowSecond", 3, 2)
        val LowThird = ActionPriority("lowThird", 3, 3)

        val ActionPriorityNone = ActionPriority("actionPriorityNone", 10, 10)
    }

    fun getPriority() = this.valueClass * 10 + this.valueInClass

    override fun toString() = "${ActionPriority::class.simpleName}($priority, ${getPriority()})"

    override fun compareTo(other: ActionPriority): Int = this.getPriority().compareTo(other.getPriority())

}