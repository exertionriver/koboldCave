import com.soywiz.korma.geom.*
import leaf.ILeaf.Companion.nodeMesh
import leaf.Leaf
import node.Node
import node.NodeLink.Companion.getNodeChildrenNodeAngles
import kotlin.math.abs
import kotlin.test.Test

class TestNodeLink {

    @ExperimentalUnsignedTypes
    @Test
    fun testConsolidateNodeLink() {
        val testNodeMesh = Leaf(initHeight = 6, position = Point(512, 512)).getLeafList().nodeMesh()

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
                    thisAngleDiff += abs(angleIdx - (it.second.degrees % angleMinDegrees).toInt() )
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

        childNodeAngles.forEach { checkNodeAngles[it.second + Angle.fromDegrees(360)] = it.first }

        childNodeAngles.forEach { checkNodeAngles[it.second - Angle.fromDegrees(360)] = it.first }

        val keepNodeAngles = mutableMapOf<Node, Angle>()

        checkNodeAngles.keys.sortedBy {
            if (it.degrees.toInt() >= 0)
                abs (( it.degrees % angleMinDegrees) - bestAngle).toInt()
                else abs ( angleMinDegrees - (it.degrees % -angleMinDegrees) - bestAngle ).toInt()
        }.forEach { checkNodeAngle ->

            var keepNodeAngle = false

            if (!keepNodeAngles.containsKey(checkNodeAngles[checkNodeAngle])) {

                val checkMod = if (checkNodeAngle.degrees.toInt() >= 0)
                    abs((checkNodeAngle.degrees % angleMinDegrees) - bestAngle).toInt()
                else abs(angleMinDegrees - (checkNodeAngle.degrees % -angleMinDegrees) - bestAngle).toInt()

                println("node:${checkNodeAngles[checkNodeAngle]} angle:${checkNodeAngle.degrees} mod:${checkMod}")

                if (keepNodeAngles.isNotEmpty()) {
                    if (checkNodeAngle.degrees.toInt() in 0..360) keepNodeAngle = true

                    keepNodeAngles.forEach { keepNode ->
                        if ( abs(checkNodeAngle.degrees - keepNode.value.degrees) < angleMinDegrees) {
                            println("angle diff : ${abs(checkNodeAngle.degrees - keepNode.value.degrees)}")
                            keepNodeAngle = false
                        }
                    }
                } else if (checkNodeAngle.degrees.toInt() in 0..360) keepNodeAngle = true

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