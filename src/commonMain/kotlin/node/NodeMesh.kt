package node

import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Point
import kotlin.random.Random

@ExperimentalUnsignedTypes
class NodeMesh(val uuid: UUID = UUID.randomUUID(Random.Default), val nodes : List<Node> ) {

    val consolidateDistance = 12.0
    val linkDistance = 24.0
/*
    fun getConsolidatedLeafNodes() : List<Node> {
        val includeNodes : MutableSet<Node> = mutableSetOf()
        val excludeNodes : MutableSet<Node> = mutableSetOf()
        var mutableNode : Node

        leafNodes.forEach { outer ->
            leafNodes.forEach { inner ->
                if (!excludeNodes.contains(outer) && !excludeNodes.contains(inner) && (inner != outer))
                when {
                    (Point.distance(inner.position, outer.position) < consolidateDistance) -> {
                        mutableNode = outer
                        inner.childNodes.let { mutableNode.childNodes.addAll(it) }
                        inner.childNodes.forEach { it.childNodes.add(mutableNode) }
                        inner.childNodes.forEach { it.childNodes.remove(inner) }
                        if (!includeNodes.contains(mutableNode)) includeNodes.add(mutableNode) //compare by UUID
                        excludeNodes.add(inner)
                    }
                    (Point.distance(inner.position, outer.position) in consolidateDistance..linkDistance) -> {
                        mutableNode = outer
                        mutableNode.childNodes.add(inner)
                        if (!includeNodes.contains(mutableNode)) includeNodes.add(mutableNode)
                    }
                    else -> if (!excludeNodes.contains(outer)) includeNodes.add(outer)
                }
            }
        }
        return includeNodes.toList().sortedBy { it.uuid.toString() }
    }

//    https://www.baeldung.com/java-k-means-clustering-algorithm

    fun getClusteredNodes(rooms : Int = 4, maxIterations : Int) : List<NodeRoom> {

        val nodeRooms = List(size = rooms) { NodeRoom(position = NodeRoom.randomPosition(leafNodes)) }

        println("init nodeRooms: $nodeRooms")

        (0 until maxIterations).toList().forEach { iteration ->
            val isLastIteration: Boolean = iteration == maxIterations - 1

            nodeRooms.forEach { roomNode -> roomNode.clearNodes() }
            leafNodes.forEach { node -> node.nearestNodeRoom(nodeRooms).nodes.add(node)}

            println("iteration $iteration:")
            println(nodeRooms)

            if (!isLastIteration) nodeRooms.forEach { nodeRoom -> nodeRoom.averagePositionWithinNodes() }
        }

        return nodeRooms
    }

    /*
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
                currentPoint = Point(currentPoint.x - nodeLength * cos(angle), currentPoint.y + nodeLength * sin(angle))

                while ( (lineEndPoint.x < currentPoint.x) && (lineEndPoint.y >= currentPoint.y)  ) {
//                    println("2) node at ${currentPoint}, angle ${angle.degrees}")
                    currentNode = Node(position = currentPoint, childNodes = mutableListOf(previousNode))
                    previousNode.childNodes.add(currentNode)
                    nodeLineList.add(currentNode)
                    previousNode = currentNode
                    currentPoint = Point(currentPoint.x - nodeLength * cos(angle), currentPoint.y + nodeLength * sin(angle))
                }
            }
            (lineEndPoint.x >= currentPoint.x) && (lineEndPoint.y < currentPoint.y) -> {
                val angle = Angle.fromRadians(atan((lineEndPoint.y - currentPoint.y) / (currentPoint.x - lineEndPoint.x)))

//                println("3) node at ${currentPoint}, angle ${angle.degrees}")
                currentPoint = Point(currentPoint.x + nodeLength * cos(angle), currentPoint.y - nodeLength * sin(angle))

                while ( (lineEndPoint.x >= currentPoint.x) && (lineEndPoint.y < currentPoint.y) ) {
//                    println("3) node at ${currentPoint}, angle ${angle.degrees}")
                    currentNode = Node(position = currentPoint, childNodes = mutableListOf(previousNode))
                    previousNode.childNodes.add(currentNode)
                    nodeLineList.add(currentNode)
                    previousNode = currentNode
                    currentPoint = Point(currentPoint.x + nodeLength * cos(angle), currentPoint.y - nodeLength * sin(angle))
                }
            }
            (lineEndPoint.x < currentPoint.x) && (lineEndPoint.y < currentPoint.y) -> {
                val angle = Angle.fromRadians(atan((currentPoint.y - lineEndPoint.y) / (currentPoint.x - lineEndPoint.x)))

//                println("4) node at ${currentPoint}, angle ${angle.degrees}")
                currentPoint = Point(currentPoint.x - nodeLength * cos(angle), currentPoint.y - 2 * sin(angle))

                while ( (lineEndPoint.x < currentPoint.x) && (lineEndPoint.y < currentPoint.y)  ) {
//                    println("4) node at ${currentPoint}, angle ${angle.degrees}")
                    currentNode = Node(position = currentPoint, childNodes = mutableListOf(previousNode))
                    previousNode.childNodes.add(currentNode)
                    nodeLineList.add(currentNode)
                    previousNode = currentNode
                    currentPoint = Point(currentPoint.x - nodeLength * cos(angle), currentPoint.y - nodeLength * sin(angle))
                }
            }
        }

        currentNode.childNodes.add(this.getNode(limitRecursion = true))

        return nodeLineList
    }

    fun getNodeList() : List<Node> =
        ( listOf( getNode() ).plus( childrenLeafNodes.flatMap { (it!! as CaveLeaf).getNodeList().plus((it as CaveLeaf).getNodeLine(this)) } )).toMutableList()


     */

    override fun toString() = "Node.NodeMesh(${uuid}) : $leafNodes"
*/
}