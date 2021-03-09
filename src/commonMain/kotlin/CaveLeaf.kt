package util.node

import Leaf
import Node
import Node.Companion.emptyNode
import Probability
import com.soywiz.korma.geom.*
import kotlin.math.atan

@ExperimentalUnsignedTypes
class CaveLeaf (initHeight : Int = 3, parentLeafNode : CaveLeaf? = null
                , val startingAngle : Angle? = null, val startingPosition : Point? = null
                , incrementPxLength : Int = 60, relativeAngle : Boolean = false
    ) : Leaf(initHeight = initHeight, parentLeafNode = parentLeafNode) {

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

    override val childrenLeafNodes = MutableList<Leaf?>(size = randChildSizeList(initHeight)) {
        if (initHeight == 0) null else CaveLeaf(
            initHeight = initHeight - 1,
            parentLeafNode = this,
            startingPosition = position,
            startingAngle = angleFromParent
        )
    }


    fun getLeafLineList() = getLeafNodeList().filter{ (it as CaveLeaf).startingPosition != null}.map {Point((it as CaveLeaf).startingPosition!!.x, it.startingPosition!!.y) to Point(it.position.x, it.position.y) }

    fun getNode(limitRecursion : Boolean = false) : Node = when {
        (limitRecursion) -> Node(uuid = uuid, position = position)
        (parentLeafNode == null) -> when {
            childrenEmpty -> Node(uuid = uuid, position = position) //singular OakLeaf, singular Node ; or limit recursion
            else -> Node(uuid = uuid, position = position, childNodes = childrenLeafNodes.map { (it!! as CaveLeaf).getNode(limitRecursion = true) }.toMutableList()) // top of OakLeaf, add Node
        }
        else -> when {
            childrenEmpty -> Node(uuid = uuid, position = position, childNodes = listOf((parentLeafNode as CaveLeaf).getNode(limitRecursion = true)).toMutableList())
            else -> Node(uuid = uuid, position = position, childNodes = childrenLeafNodes.map { (it!! as CaveLeaf).getNode(limitRecursion = true) }.plus((parentLeafNode as CaveLeaf).getNode(limitRecursion = true)).toMutableList())
        }
    }

    fun getNodeLine(parentLeaf : CaveLeaf) : List<Node> {

        val nodeLineList = mutableListOf<Node>()
        var currentPoint = Point(parentLeaf.position.x, parentLeaf.position.y)
        val lineEndPoint = Point(this.position.x, this.position.y)
        val nodeLength = randNodeLengthFromParent()

        var previousNode = parentLeaf.getNode(limitRecursion = true)
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
        ( listOf( getNode() ).plus( childrenLeafNodes.flatMap { (it!! as CaveLeaf).getNodeList().plus((it as CaveLeaf).getNodeLine(this)) } )).toMutableList()

    //nodeLineList handled in NodeMesh

    override fun toString() = "CaveLeaf($uuid) : ${initHeight}, ${childrenLeafNodes.size}, $lengthFromParent, $angleFromParent, $position"

    companion object {
        fun randOakLengthFromParent() : Int = ProbabilitySelect.psAccumulating(listOf(1, 2, 3, 5, 7)).getSelectedProbability()!! // in pxIncrements

        fun randNodeLengthFromParent() : Int = 30 //util.conditions.ProbabilitySelect.psAccumulating(listOf(16, 18, 14, 20, 12, 22)).getSelectedProbability()!! //in ft
    }
}