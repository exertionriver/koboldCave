package node

import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Point
import kotlin.random.Random

//aka 'kmeans centroid'
@ExperimentalUnsignedTypes
class NodeRoom(val uuid: UUID = UUID.randomUUID(), val position : Point, val nodes : MutableList<Node> = mutableListOf() ) {

    val description = "room${Random.nextInt(512)}"

    fun clearNodes() = nodes.clear()

    fun averagePositionWithinNodes() {
        val averageX = nodes.map {node -> node.position.x.toInt()}.average()
        val averageY = nodes.map {node -> node.position.y.toInt()}.average()

        position.x = averageX
        position.y = averageY
    }

    override fun toString() = "Node.NodeRoom(${uuid}) : ($position) [${nodes.size}]"// $nodes"

    companion object {
        fun emptyNodeRoom() = NodeRoom(position = Point(0, 0))

        fun randomPosition(nodes : List<Node>) : Point {

            val minXY = Point(10000, 10000)
            val maxXY = Point(0, 0)

            nodes.forEach { node ->
                when {
                    (node.position.x > maxXY.x) -> maxXY.x = node.position.x
                    (node.position.y > maxXY.y) -> maxXY.y = node.position.y
                    (node.position.x < minXY.x) -> minXY.x = node.position.x
                    (node.position.y < minXY.x) -> minXY.y = node.position.y
                }
            }

            println("max: $maxXY, min: $minXY")

                val randomXInRange = Random.nextInt(maxXY.x.toInt() - minXY.x.toInt() ) + minXY.x.toInt()
                val randomYInRange = Random.nextInt(maxXY.y.toInt() - minXY.y.toInt() ) + minXY.y.toInt()

            println("randomX: $randomXInRange, randomY: $randomYInRange")

            return Point(randomXInRange, randomYInRange)
        }
    }

    override fun equals(other: Any?): Boolean {
        return (this.uuid == (other as NodeRoom).uuid)
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}