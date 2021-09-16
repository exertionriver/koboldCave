package org.river.exertion.koboldCave.node

import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.condition.ProbabilitySelect

class NodeAttributes {

    enum class NodeType {
        CENTROID //usually not within rendered mesh nodes
        , START
        , EXIT
        , OPEN }
    var nodeType : NodeType = NodeType.OPEN

    enum class NodeObstacle {
        HEAVY { override fun getChallenge() = 80 }
        , MEDIUM { override fun getChallenge() = 50 }
        , LIGHT { override fun getChallenge() = 20 }
        , NONE { override fun getChallenge() = 1 } ;
        abstract fun getChallenge() : Int
    }
    var nodeObstacle : NodeObstacle = NodeObstacle.NONE

    enum class NodeElevation {
        HIGH_POS { override fun getHeight() = 10f }
        , MEDIUM_POS { override fun getHeight() = 6f }
        , LOW_POS { override fun getHeight() = 2f }
        , NONE { override fun getHeight() = 0f }
        , LOW_NEG { override fun getHeight() = -2f }
        , MEDIUM_NEG { override fun getHeight() = -6f }
        , HIGH_NEG { override fun getHeight() = -10f } ;
        abstract fun getHeight() : Float
    }
    var nodeElevation : NodeElevation = NodeElevation.NONE

    override fun toString(): String {
        return "${super.toString()}, $nodeType"
    }

    companion object {
        fun getProbNodeObstacle() : NodeObstacle =
            ProbabilitySelect(
                mapOf(
                    NodeObstacle.HEAVY to Probability(5, 0),
                    NodeObstacle.MEDIUM to Probability(15, 0),
                    NodeObstacle.LIGHT to Probability(30, 0),
                    NodeObstacle.NONE to Probability(50, 0)
                )
            ).getSelectedProbability()!!

        fun getProbNodeElevation() : NodeElevation =
            ProbabilitySelect(
                mapOf(
                    NodeElevation.HIGH_POS to Probability(5, 0),
                    NodeElevation.MEDIUM_POS to Probability(10, 0),
                    NodeElevation.LOW_POS to Probability(20, 0),
                    NodeElevation.NONE to Probability(40, 0),
                    NodeElevation.LOW_NEG to Probability(20, 0),
                    NodeElevation.MEDIUM_NEG to Probability(10, 0),
                    NodeElevation.HIGH_NEG to Probability(5, 0)
                )
            ).getSelectedProbability()!!
    }
}