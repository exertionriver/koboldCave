import Node.Companion.emptyNode
import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.*
import kotlin.math.atan
import kotlin.math.max
import kotlin.random.Random

@ExperimentalUnsignedTypes
class OakLeaf(val uuid: UUID = UUID.randomUUID(Random.Default)
                , val initHeight : Int = 3
                , val parentLeafNode : OakLeaf? = null
                , val startingAngle : Angle? = null
                , val startingPosition : Point? = null
                , val incrementPxLength : Int = 60
                , val relativeAngle : Boolean = false
            ) {

    val lengthFromParent : Int = when {
        (parentLeafNode == null) -> 0
        (initHeight > 0) -> randOakLengthFromParent()
        else -> 1
    }

    val angleFromParent : Angle = when {
        (startingAngle == null) -> Angle.fromDegrees(0) //0 == up
        (initHeight >= 0) -> startingAngle + Angle.fromDegrees(Probability(0, 30).getValue())
        else -> startingAngle
    }

    val position : Point = when {
        (startingPosition == null) -> Point(512, 512)
        (initHeight >= 0) -> {
            if (relativeAngle) {
                when {
                    (angleFromParent >= Angle.fromDegrees(270) && angleFromParent < Angle.fromDegrees(90) ) ->
                        Point(startingPosition.x + lengthFromParent * incrementPxLength * sin(angleFromParent)
                            , startingPosition.y - lengthFromParent * incrementPxLength * cos(angleFromParent)
                        )
                    else ->
                        Point(startingPosition.x + lengthFromParent * incrementPxLength * sin(angleFromParent)
                            , startingPosition.y + lengthFromParent * incrementPxLength * cos(angleFromParent)
                        )
                }
            }
            else //grow from bottom of screen up
                Point(startingPosition.x + lengthFromParent * incrementPxLength * sin(angleFromParent)
                    , startingPosition.y - lengthFromParent * incrementPxLength * cos(angleFromParent)
                )
        }
        else -> Point(startingPosition.x, startingPosition.y)
    }

    val childrenLeafNodes = MutableList(size = randOakNodeSizeList(initHeight)) { if (initHeight == 0) null else OakLeaf(
            initHeight = initHeight - 1,
            parentLeafNode = this,
            startingPosition = position,
            startingAngle = angleFromParent
        )
    }

    val childrenEmpty = childrenLeafNodes.isNullOrEmpty()

    fun getLeafNodeList() : List<OakLeaf> = listOf(this) + childrenLeafNodes.flatMap { it!!.getLeafNodeList() }

    fun getLeafLineList() = getLeafNodeList().filter{ it.startingPosition != null}.map {Point(it.startingPosition!!.x, it.startingPosition.y) to Point(it.position.x, it.position.y) }

    fun getNode(limitRecursion : Boolean = false) : Node = when {
        (limitRecursion) -> Node(uuid = uuid, position = position)
        (parentLeafNode == null) -> when {
            childrenEmpty -> Node(uuid = uuid, position = position) //singular OakLeaf, singular Node ; or limit recursion
            else -> Node(uuid = uuid, position = position, childNodes = childrenLeafNodes.map { it!!.getNode(limitRecursion = true) }.toMutableList()) // top of OakLeaf, add Node
        }
        else -> when {
            childrenEmpty -> Node(uuid = uuid, position = position, childNodes = listOf(parentLeafNode.getNode(limitRecursion = true)).toMutableList())
            else -> Node(uuid = uuid, position = position, childNodes = childrenLeafNodes.map { it!!.getNode(limitRecursion = true) }.plus(parentLeafNode.getNode(limitRecursion = true)).toMutableList())
        }
    }

    fun getNodeLine(parentOakLeaf : OakLeaf) : List<Node> {

        val nodeLineList = mutableListOf<Node>()
        var currentPoint = Point(parentOakLeaf.position.x, parentOakLeaf.position.y)
        val lineEndPoint = Point(this.position.x, this.position.y)
        val nodeLength = randNodeLengthFromParent()

        var previousNode = parentOakLeaf.getNode(limitRecursion = true)
        var currentNode = emptyNode()

//        println (this.uuid.toString() + "nodeLine + [${parentOakLeaf.position}, ${this.position}] step $nodeLength")

        when {
            (lineEndPoint.x >= currentPoint.x) && (lineEndPoint.y >= currentPoint.y) -> {
                val angle = Angle.fromRadians(atan( (lineEndPoint.y - currentPoint.y) / (lineEndPoint.x - currentPoint.x) ) )

//                println("1) node at ${currentPoint}, angle ${angle.degrees}")
                currentPoint = Point(currentPoint.x + nodeLength * cos(angle), currentPoint.y + nodeLength * sin(angle))

                while ((lineEndPoint.x >= currentPoint.x) && (lineEndPoint.y >= currentPoint.y) ) {
//                    println("1) node at ${currentPoint}, angle ${angle.degrees}")
                    currentNode = Node(position = currentPoint, childNodes = mutableListOf(previousNode))
                    previousNode.childNodes.add(currentNode)
                    nodeLineList.add(currentNode)
                    previousNode = currentNode
                    currentPoint = Point(currentPoint.x + nodeLength * cos(angle), currentPoint.y + nodeLength * sin(angle))
                }
            }
            (lineEndPoint.x < currentPoint.x) && (lineEndPoint.y >= currentPoint.y) -> {
                val angle = Angle.fromRadians(atan((currentPoint.y - lineEndPoint.y) / (lineEndPoint.x - currentPoint.x)))

//                println("2) node at ${currentPoint}, angle ${angle.degrees}")
                currentPoint = Point(currentPoint.x - 2 * cos(angle), currentPoint.y + 2 * sin(angle))

                while ( (lineEndPoint.x < currentPoint.x) && (lineEndPoint.y >= currentPoint.y)  ) {
//                    println("2) node at ${currentPoint}, angle ${angle.degrees}")
                    currentNode = Node(position = currentPoint, childNodes = mutableListOf(previousNode))
                    previousNode.childNodes.add(currentNode)
                    nodeLineList.add(currentNode)
                    previousNode = currentNode
                    currentPoint = Point(currentPoint.x - 2 * cos(angle), currentPoint.y + 2 * sin(angle))
                }
            }
            (lineEndPoint.x >= currentPoint.x) && (lineEndPoint.y < currentPoint.y) -> {
                val angle = Angle.fromRadians(atan((lineEndPoint.y - currentPoint.y) / (currentPoint.x - lineEndPoint.x)))

//                println("3) node at ${currentPoint}, angle ${angle.degrees}")
                currentPoint = Point(currentPoint.x + 2 * cos(angle), currentPoint.y - 2 * sin(angle))

                while ( (lineEndPoint.x >= currentPoint.x) && (lineEndPoint.y < currentPoint.y) ) {
//                    println("3) node at ${currentPoint}, angle ${angle.degrees}")
                    currentNode = Node(position = currentPoint, childNodes = mutableListOf(previousNode))
                    previousNode.childNodes.add(currentNode)
                    nodeLineList.add(currentNode)
                    previousNode = currentNode
                    currentPoint = Point(currentPoint.x + 2 * cos(angle), currentPoint.y - 2 * sin(angle))
                }
            }
            (lineEndPoint.x < currentPoint.x) && (lineEndPoint.y < currentPoint.y) -> {
                val angle = Angle.fromRadians(atan((currentPoint.y - lineEndPoint.y) / (currentPoint.x - lineEndPoint.x)))

//                println("4) node at ${currentPoint}, angle ${angle.degrees}")
                currentPoint = Point(currentPoint.x - 2 * cos(angle), currentPoint.y - 2 * sin(angle))

                while ( (lineEndPoint.x < currentPoint.x) && (lineEndPoint.y < currentPoint.y)  ) {
//                    println("4) node at ${currentPoint}, angle ${angle.degrees}")
                    currentNode = Node(position = currentPoint, childNodes = mutableListOf(previousNode))
                    previousNode.childNodes.add(currentNode)
                    nodeLineList.add(currentNode)
                    previousNode = currentNode
                    currentPoint = Point(currentPoint.x - 2 * cos(angle), currentPoint.y - 2 * sin(angle))
                }
            }
        }

        currentNode.childNodes.add(this.getNode(limitRecursion = true))

        return nodeLineList
    }

    fun getNodeList() : List<Node> =
         ( listOf( getNode() ).plus( childrenLeafNodes.flatMap { it!!.getNodeList().plus(it.getNodeLine(this)) } )).toMutableList()

    //nodeLineList handled in NodeMesh

    fun getCurrentHeight() : Int =
        if (childrenLeafNodes == emptyOakLeaf().childrenLeafNodes) 0
        else (1 + childrenLeafNodes.map { it!!.getCurrentHeight() }.reduce { maxVal : Int, element -> max(maxVal, element) })

    override fun toString() = "OakLeaf(${uuid}) : ${initHeight}, ${childrenLeafNodes.size}, $lengthFromParent, $angleFromParent, $position " //+ childrenLeafNodes.iterator().forEach { if (it != null) println(it) }

    companion object {

        fun emptyOakLeaf() = OakLeaf(initHeight = 0)

        fun randOakLengthFromParent() : Int = ProbabilitySelect.psAccumulating(listOf(1, 2, 3, 5, 7)).getSelectedProbability()!! // in pxIncrements

        fun randNodeLengthFromParent() : Int = 30 //ProbabilitySelect.psAccumulating(listOf(16, 18, 14, 20, 12, 22)).getSelectedProbability()!! //in ft

        fun randOakNodeSizeList(height : Int): Int {
            return when {
                (height > 2) -> ProbabilitySelect(
                    mapOf(
                        "1" to Probability(60, 0),
                        "2" to Probability(25, 0),
                        "3" to Probability(5, 0)
                    )
                ).getSelectedProbability()!!.toInt()
                (height > 0) -> ProbabilitySelect(
                    mapOf(
                        "1" to Probability(20, 0),
                        "2" to Probability(30, 0),
                        "3" to Probability(50, 0)
                    )
                ).getSelectedProbability()!!.toInt()
                else -> 0
            }
        }

    }

}