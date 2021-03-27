package node

import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import leaf.ILeaf

@ExperimentalUnsignedTypes
interface INodeMesh {

    val uuid : UUID

    val nodes : MutableList<Node>

    val nodeLinks : MutableList<NodeLink>

    fun getNode(uuid : UUID) : Node? = nodes.firstOrNull { node -> node.uuid == uuid }

    fun getNodeLink(firstUuid: UUID, secondUuid: UUID) : NodeLink? = nodeLinks.firstOrNull { nodeLink -> nodeLink.firstNodeUuid == firstUuid && nodeLink.secondNodeUuid == secondUuid }

    fun getNodeLinks(uuid: UUID) : List<NodeLink>? = nodeLinks.filter { nodeLink -> nodeLink.firstNodeUuid == uuid || nodeLink.secondNodeUuid == uuid }

    fun removeNode(uuid : UUID) = nodes.remove( getNode(uuid) )

    fun removeNodeLink(firstUuid: UUID, secondUuid: UUID) = nodeLinks.remove( getNodeLink(firstUuid, secondUuid) )

    fun updateNode(node : Node) {
        removeNode(node.uuid)
        nodes.add(node)
    }

    fun updateNodeLink(nodeLink : NodeLink) {
        removeNodeLink(nodeLink.firstNodeUuid, nodeLink.secondNodeUuid)
        nodeLinks.add(nodeLink)
    }

    //replaces firstUuid node, removes secondUuid node; does not handle updates to NodeLinks
    fun consolidateNodes(firstUuid : UUID, secondUuid : UUID) {
        val firstNode = getNode(firstUuid)
        val secondNode = getNode(secondUuid)

        if ( (firstNode != null) && (secondNode != null) ) {

            println("pre-consolidate firstNode: $firstNode")

            //positioned between current first and second nodes, with children uuids of both

            val consolidatedNodes : MutableList<UUID>? = firstNode.childNodeUuids
            secondNode.childNodeUuids?.let { consolidatedNodes?.addAll(it) }

            //remove any crossUuids between first and second, if present
            firstNode.childNodeUuids?.remove(secondUuid)
            secondNode.childNodeUuids?.remove(firstUuid)

            updateNode(
                Node( firstNode
                    , updPosition = Point.middle(firstNode.position, secondNode.position)
                    , updChildNodeUuids = consolidatedNodes
                )
            )

            println("post-consolidate firstNode: $firstNode")

            println("pre-consolidate secondNode: $secondNode")

            //update children of second node to point to first (updated) node
            secondNode.childNodeUuids?.forEach { secondNodeChild ->
                val updateSecondNodeChild : Node? = getNode(secondNodeChild)

                if (updateSecondNodeChild != null) {

                    println("consolidating secondNode child: $updateSecondNodeChild")

                    updateSecondNodeChild.childNodeUuids?.remove(secondUuid)
                    updateSecondNodeChild.childNodeUuids?.add(firstUuid)
                    updateNode(updateSecondNodeChild)

                    println("done consolidating secondNode child: $updateSecondNodeChild")
                }
            }

//            println("post-consolidate secondNode: $secondNode")

            //remove old second node
            removeNode(secondUuid)
        }
    }

    //consolidateNode should be called first. node at firstUuid should contain all links for node at secondUuid
    fun consolidateNodeLinks(firstUuid : UUID, secondUuid : UUID) {
        val firstNode = getNode(firstUuid)

        if ( (firstNode != null) ) {

            //point links containing secondNodeUuid to firstNodeUuid
            getNodeLinks(secondUuid)?.forEach { nodeLink ->
//                println("pre-consolidate nodeLink: $nodeLink")

                if (nodeLink.firstNodeUuid == secondUuid) updateNodeLink(
                    NodeLink(nodeLink, updFirstNodeUuid = firstNode.uuid)
                )
                if (nodeLink.secondNodeUuid == secondUuid) updateNodeLink(
                    NodeLink(nodeLink, updSecondNodeUuid = firstNode.uuid)
                )

//                println("post-consolidate nodeLink: $nodeLink")
            }

            //remove circular links
            removeNodeLink(firstNode.uuid, firstNode.uuid)
        }
    }


    fun consolidateNodes() {
//        println("checking for nodes to consolidate...")

        nodeLinks.filter { link -> link.distance <= consolidateNodeDistance }.forEach { consolidateNodeLink ->
            consolidateNodes(consolidateNodeLink.firstNodeUuid, consolidateNodeLink.secondNodeUuid)
            consolidateNodeLinks(consolidateNodeLink.firstNodeUuid, consolidateNodeLink.secondNodeUuid)
        }
    }

    fun linkNodes() {
        println("checking for nodes to link...")

        nodes.forEach { outer ->
            nodes.forEach { inner ->
                if ( ( getNodeLink(outer.uuid, inner.uuid) == null) && (outer.uuid != inner.uuid) ) {
                    if (Point.distance(inner.position, outer.position).toInt() <= linkNodeDistance) {
                        nodeLinks.add(NodeLink(inner.uuid, outer.uuid, Point.distance(inner.position, outer.position)
                            , Angle.between(inner.position, outer.position), Angle.between(outer.position, inner.position)))
                    }
                }
            }
        }
    }


//    https://www.baeldung.com/java-k-means-clustering-algorithm

    fun getClusteredNodes(rooms : Int = 4, maxIterations : Int) : List<NodeRoom> {

        val nodeRooms = List(size = rooms) { NodeRoom(position = NodeRoom.randomPosition(nodes)) }

        println("init nodeRooms: $nodeRooms")

        (0 until maxIterations).toList().forEach { iteration ->
            val isLastIteration: Boolean = iteration == maxIterations - 1

            nodeRooms.forEach { roomNode -> roomNode.clearNodes() }
            nodes.forEach { node -> node.nearestNodeRoom(nodeRooms).nodes.add(node)}

            println("iteration $iteration:")
            println(nodeRooms)

            if (!isLastIteration) nodeRooms.forEach { nodeRoom -> nodeRoom.averagePositionWithinNodes() }
        }

        return nodeRooms
    }

    fun getNodeLineList() : List<Pair<Point, Point>?> {

        val returnNodeLineList : MutableList<Pair<Point, Point>> = mutableListOf()

        nodeLinks.sortedBy {
                link -> link.firstNodeUuid.toString() }.filter {
                    sortedLink -> sortedLink.firstNodeUuid.toString() < sortedLink.secondNodeUuid.toString() }.forEach {
                        filteredLink -> if ( getNode(filteredLink.firstNodeUuid) != null && getNode(filteredLink.secondNodeUuid) != null)
                            returnNodeLineList.add(Pair(getNode(filteredLink.firstNodeUuid)!!.position, getNode(filteredLink.secondNodeUuid)!!.position) )
                }

        return returnNodeLineList
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


     */

    companion object {
        val consolidateNodeDistance = ILeaf.LeafDistancePx / 2
        val linkNodeDistance = ILeaf.LeafDistancePx
    }
}