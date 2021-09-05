package org.river.exertion.koboldCave.node

import org.river.exertion.Point
import org.river.exertion.koboldCave.Probability
import org.river.exertion.koboldCave.ProbabilitySelect
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.NextDistancePx

class NodeAttributes {

    enum class NodeType {
        CENTROID //usually not within rendered mesh nodes
        , START
        , EXIT
        , OPEN }
    var nodeType : NodeType = NodeType.OPEN

    enum class NodeObstacle {
        HEAVY { override fun getChallenge() = 70 }
        , MEDIUM { override fun getChallenge() = 50 }
        , LIGHT { override fun getChallenge() = 30 }
        , NONE { override fun getChallenge() = 0 } ;
        abstract fun getChallenge() : Int
    }
    var nodeObstacle : NodeObstacle = NodeObstacle.NONE

    override fun toString(): String {
        return "${super.toString()}, $nodeType"
    }

    companion object {
        fun getRandomObstacleChallenge() : NodeObstacle =
            ProbabilitySelect(
                mapOf(
                    NodeObstacle.HEAVY to Probability(5, 0),
                    NodeObstacle.MEDIUM to Probability(15, 0),
                    NodeObstacle.LIGHT to Probability(30, 0),
                    NodeObstacle.NONE to Probability(50, 0)
                )
            ).getSelectedProbability()!!
    }
}