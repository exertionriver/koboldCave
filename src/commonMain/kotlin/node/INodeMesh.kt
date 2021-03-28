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

    fun getNodeLink(firstUuid: UUID, secondUuid: UUID) : NodeLink? = nodeLinks.getNodeLink(firstUuid, secondUuid)

    fun isNodeLink(firstUuid: UUID, secondUuid: UUID) : Boolean = ( getNodeLink(firstUuid, secondUuid) != null && getNodeLink(secondUuid, firstUuid) != null )

    fun getThisNodeLinkUuids(uuid: UUID) : List<UUID>? = nodeLinks.filter { nodeLink -> nodeLink.firstNodeUuid == uuid }.map{ filteredLink -> filteredLink.secondNodeUuid }

    fun getFullNodeLinks(uuid: UUID) : List<NodeLink>? = nodeLinks.getFullNodeLinks(uuid)

    fun removeNode(uuid : UUID) {
        nodes.remove( getNode(uuid) )
        getFullNodeLinks(uuid)?.let { nodeLinks.removeAll(it) }
    }

    fun removeNodeLink(firstUuid: UUID, secondUuid: UUID) = nodeLinks.remove( getNodeLink(firstUuid, secondUuid) )

    fun removeFullNodeLinks(uuid : UUID) = getFullNodeLinks(uuid)?.let { nodeLinks.removeAll(it) }

    fun addNodeLinks(firstUuid: UUID, secondUuid: UUID) = nodeLinks.addNodeLinks(firstUuid, secondUuid)

    fun updateNode(node : Node) {
        removeNode(node.uuid)
        nodes.add(node)
    }

    //replaces firstUuid node, removes secondUuid node; does not handle updates to NodeLinks
    fun consolidateNode(firstUuid : UUID, secondUuid : UUID)  {
        val firstNode = getNode(firstUuid)
        val secondNode = getNode(secondUuid)

        if ( (firstNode != null) && (secondNode != null) ) {

            //get UUIDs that second node links to
            val secondNodeUuids = getThisNodeLinkUuids(secondUuid)

            //cycle through second node UUIDs, create link to first UUID node if none exists
            if (!secondNodeUuids.isNullOrEmpty()) {
                secondNodeUuids.forEach { secondNodeChild -> if (!isNodeLink(firstUuid, secondNodeChild)) addNodeLinks(firstUuid, secondNodeChild) }
            }

            //remove all links to / from second Node
            removeFullNodeLinks(secondUuid)

            //update first node with new position, between two nodes
            updateNode(
                Node( firstNode
                    , updPosition = Point.middle(firstNode.position, secondNode.position)
                )
            )

            //remove second node
            removeNode(secondUuid)
        }
    }

    fun consolidateNodes() {
//        println("checking for nodes to consolidate...")

        nodeLinks.filter { link -> link.getDistance(nodes)!! <= consolidateNodeDistance }.forEach { consolidateNodeLink ->
            consolidateNode(consolidateNodeLink.firstNodeUuid, consolidateNodeLink.secondNodeUuid)
        }
    }

    fun linkNodes(linkOrphans : Boolean = true) = nodes.linkNodes(linkOrphans)

//    https://www.baeldung.com/java-k-means-clustering-algorithm

    fun getClusteredNodes(rooms : Int = 4, maxIterations : Int) : List<NodeRoom> {

        val nodeRooms = List(size = rooms) { NodeRoom(position = NodeRoom.randomPosition(nodes)) }

//        println("init nodeRooms: $nodeRooms")

        (0 until maxIterations).toList().forEach { iteration ->
            val isLastIteration: Boolean = iteration == maxIterations - 1

            nodeRooms.forEach { roomNode -> roomNode.clearNodes() }
            nodes.forEach { node -> node.nearestNodeRoom(nodeRooms).nodes.add(node)}

//            println("iteration $iteration:")
//            println(nodeRooms)

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
        val consolidateNodeDistance = ILeaf.LeafDistancePx / 8
        val linkNodeDistance = ILeaf.LeafDistancePx

        fun MutableList<NodeLink>.getNodeLink(firstUuid: UUID, secondUuid: UUID) : NodeLink? = this.firstOrNull { nodeLink -> nodeLink.firstNodeUuid == firstUuid && nodeLink.secondNodeUuid == secondUuid }

        fun MutableList<NodeLink>.getFullNodeLinks(uuid: UUID) : List<NodeLink>? = this.filter { nodeLink -> nodeLink.firstNodeUuid == uuid || nodeLink.secondNodeUuid == uuid }

        fun MutableList<NodeLink>.addNodeLinks(firstUuid: UUID, secondUuid: UUID) = this.addAll( mutableListOf(NodeLink(firstUuid, secondUuid), NodeLink(secondUuid, firstUuid) ) )

        fun MutableList<Node>.linkNodes(linkOrphans : Boolean = true) : MutableList<NodeLink> {

        println("checking for nodes to re-link...")

            val nodeLinks : MutableList<NodeLink> = mutableListOf()
            lateinit var closestNode : Node

            this.forEach { outer ->
                closestNode = Node (outer, updPosition = outer.position + Point(10000, 10000) ) //faraway point

                println("linking outer: $outer, $closestNode")
                this.forEach { inner ->
                    //in case outer node is orphaned
                    if ( ( nodeLinks.getNodeLink(outer.uuid, inner.uuid) == null) && (outer.uuid != inner.uuid) ) {
                        if ( Point.distance(inner.position, outer.position) <= Point.distance(outer.position, closestNode.position) ) {
                            closestNode = inner
                            println("new closestNode: $outer, $closestNode")
                        }

                        if (Point.distance(inner.position, outer.position).toInt() <= linkNodeDistance) {
                            nodeLinks.addNodeLinks(outer.uuid, inner.uuid)
                        }
                    }
                }
                //if outer node is orphaned, link to closest node
                if ( (this.size > 1) && (nodeLinks.getFullNodeLinks(outer.uuid).isNullOrEmpty() ) && linkOrphans ) {
                    println ("adding link for orphan: $outer, $closestNode")
                    nodeLinks.addNodeLinks(outer.uuid, closestNode.uuid)
                }
            }
            return nodeLinks
        }
    }
}