package koboldCave

import org.junit.jupiter.api.Test
import org.river.exertion.Angle
import org.river.exertion.Point
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.nodeMesh
import org.river.exertion.koboldCave.leaf.Leaf
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNodeChildrenNodeAngles
import kotlin.math.abs

class TestNodeLink {

    @ExperimentalUnsignedTypes
    @Test
    fun testConsolidateNodeLink() {
        val testNodeMesh = Leaf(topHeight = 6, position = Point(512F, 512F)).nodeMesh()

        val consolidatedLinks : MutableList<Pair<Node, Angle>> = mutableListOf()

        val angleMinDegrees = 30

        val randomNode = testNodeMesh.getRandomNode()

            println("node id: ${randomNode.uuid}")

            val childNodeAngles = testNodeMesh.nodeLinks.getNodeChildrenNodeAngles(testNodeMesh.nodes, randomNode.uuid)
            var bestAngle = 0
            var bestAngleDiff = childNodeAngles.size * angleMinDegrees

            (0 until angleMinDegrees).forEach { angleIdx ->
                var thisAngleDiff = 0

                childNodeAngles.forEach {
                //    println ("angle modded: ${(it.second.degrees % angleMinDegrees).toInt()} ")
                    thisAngleDiff += abs(angleIdx - (it.second % angleMinDegrees).toInt() )
                }

                println ("thisAngleDiff @ $angleIdx: $thisAngleDiff")

                if (thisAngleDiff <= bestAngleDiff) {
                    bestAngleDiff = thisAngleDiff
                    bestAngle = angleIdx
                }

                println ("bestAngleDiff @ $bestAngle: $bestAngleDiff")
            }

        println ("final bestAngleDiff @ $bestAngle: $bestAngleDiff")

        val checkNodeAngles = mutableMapOf<Angle, Node>()

        childNodeAngles.forEach { checkNodeAngles[it.second] = it.first }

        childNodeAngles.forEach { checkNodeAngles[it.second + 360F] = it.first }

        childNodeAngles.forEach { checkNodeAngles[it.second - 360F] = it.first }

        val keepNodeAngles = mutableMapOf<Node, Angle>()

        checkNodeAngles.keys.sortedBy {
            if (it.toInt() >= 0)
                abs (( it % angleMinDegrees) - bestAngle).toInt()
                else abs ( angleMinDegrees - (it % -angleMinDegrees) - bestAngle ).toInt()
        }.forEach { checkNodeAngle ->

            var keepNodeAngle = false

            if (!keepNodeAngles.containsKey(checkNodeAngles[checkNodeAngle])) {

                val checkMod = if (checkNodeAngle.toInt() >= 0)
                    abs((checkNodeAngle % angleMinDegrees) - bestAngle).toInt()
                else abs(angleMinDegrees - (checkNodeAngle % -angleMinDegrees) - bestAngle).toInt()

                println("node:${checkNodeAngles[checkNodeAngle]} angle:${checkNodeAngle} mod:${checkMod}")

                if (keepNodeAngles.isNotEmpty()) {
                    if (checkNodeAngle.toInt() in 0..360) keepNodeAngle = true

                    keepNodeAngles.forEach { keepNode ->
                        if ( abs(checkNodeAngle - keepNode.value) < angleMinDegrees) {
                            println("angle diff : ${abs(checkNodeAngle - keepNode.value)}")
                            keepNodeAngle = false
                        }
                    }
                } else if (checkNodeAngle.toInt() in 0..360) keepNodeAngle = true

                if (keepNodeAngle) {
                    println("adding ${checkNodeAngles[checkNodeAngle]} = $checkNodeAngle")
                    keepNodeAngles[checkNodeAngles[checkNodeAngle]!!] = checkNodeAngle
                }
            }
        }
        keepNodeAngles.forEach { println ("keepNodesAngles: ${it.key} : ${it.value}")}

        val removeNodeAngles = mutableMapOf<Node, Angle>()

        childNodeAngles.forEach { if (!keepNodeAngles.containsKey(it.first)) removeNodeAngles[it.first] = it.second }

        removeNodeAngles.forEach { println ("removeNodesAngles: ${it.key} : ${it.value}")}
    }
}