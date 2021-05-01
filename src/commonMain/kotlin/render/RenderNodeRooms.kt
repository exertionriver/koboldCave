package render

import node.NodeMesh
import com.soywiz.korge.Korge
import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.circle
import com.soywiz.korma.geom.vector.line
import kotlinx.coroutines.delay
import leaf.ILeaf.Companion.nodeMesh
import leaf.Leaf
import node.INodeMesh
import node.INodeMesh.Companion.addMesh
import node.Node
import node.Node.Companion.angleBetween
import node.Node.Companion.averagePositionWithinNodes
import node.Node.Companion.getFarthestNode
import node.Node.Companion.getRandomNode
import node.Node.Companion.moveNodes
import node.Node.Companion.nearestNodesOrderedAsc
import node.Node.Companion.scaleNodes

object RenderNodeRooms {
/*
    @ExperimentalUnsignedTypes
    suspend fun renderNodeRooms(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        var funIdx = 0
        val funSize = 4

        while ( (funIdx >= 0) && (funIdx < funSize) ) {
//            println ("funMapIdx : $funIdx")
            commandViews[CommandView.NODE_UUID_TEXT].setText(CommandView.NODE_UUID_TEXT.label())
            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(CommandView.NODE_DESCRIPTION_TEXT.label())

            when (funIdx) {
                0 -> if ( renderNodeRoomsSizes(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                1 -> if ( renderNodeRoomsSetCentroids(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                2 -> if ( renderNodeRoomsOrphanAdoptingDiff(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                3 -> if ( renderNodeRoomsElaboration(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
            }
        }

        return if (funIdx > 0) ButtonCommand.NEXT else ButtonCommand.PREV
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeRoomsSizes(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        graphics {

            RenderNodeRooms.textView = text(text = "click a node to get uuid", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)

            RenderNavigation.roomView = text(text = "current room", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 70)

            val roomMesh = INodeMesh.buildRoomMesh(Point(512, 512), height = 4)

//            println("drawing lines")
            for (nodeLine in roomMesh.getNodeLineList()) {

                stroke(RenderPalette.BackColors[1], StrokeInfo(thickness = 3.0)) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

//            println("drawing nodes")
            roomMesh.nodes.forEach { node ->

                //      println(node.description)

                val numberRegex = Regex("\\d+")

                val colorIdx = numberRegex.find(node.description, 0)?.value?.toInt() ?: 0

                circle { position(node.position)
                    radius = 5.0
                    color = RenderPalette.ForeColors[colorIdx % RenderPalette.ForeColors.size]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                        RenderNavigation.updateRoomText(node.description)
                    }
                }
            }

            roomMesh.centroids.forEach { node ->

                //      println(node.description)

                val numberRegex = Regex("\\d+")

                val colorIdx = numberRegex.find(node.description, 0)?.value?.toInt() ?: 0

                circle { position(node.position)
                    radius = 10.0
                    color = RenderPalette.ForeColors[colorIdx % RenderPalette.ForeColors.size]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                        RenderNavigation.updateRoomText(node.description)
                    }
                }
            }
        }
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeRoomsSetCentroids(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        graphics {

            RenderNodeRooms.textView = text(text = "click a node to get uuid", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)

            RenderNavigation.roomView = text(text = "current room", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 70)

            val centroidMesh = INodeMesh.buildRoomMesh(Point(200, 200), height = 2)

            //           println("centroidMesh:$centroidMesh")

            for (nodeLine in centroidMesh.getNodeLineList()) {

                stroke(RenderPalette.BackColors[2], StrokeInfo(thickness = 3.0)) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

            centroidMesh.nodes.forEach { node ->

                circle { position(node.position)
                    radius = 5.0
                    color = RenderPalette.ForeColors[2]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                        RenderNavigation.updateRoomText(node.description)
                    }
                }
            }

            val roomMesh = INodeMesh.buildCentroidRoomMesh(
                height = 3,
                centroids = centroidMesh.nodes.moveNodes(Point(312, 312)).scaleNodes(scale = 1.5)
            )

//            println("drawing lines")
            for (nodeLine in roomMesh.getNodeLineList()) {

                stroke(RenderPalette.BackColors[1], StrokeInfo(thickness = 3.0)) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

//            println("drawing nodes")
            roomMesh.nodes.forEach { node ->

                //      println(node.description)

                val numberRegex = Regex("\\d+")

                val colorIdx = numberRegex.find(node.description, 0)?.value?.toInt() ?: 0

                circle { position(node.position)
                    radius = 5.0
                    color = RenderPalette.ForeColors[colorIdx % RenderPalette.ForeColors.size]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                        RenderNavigation.updateRoomText(node.description)
                    }
                }
            }

            roomMesh.centroids.forEach { node ->

                //      println(node.description)

                val numberRegex = Regex("\\d+")

                val colorIdx = numberRegex.find(node.description, 0)?.value?.toInt() ?: 0

                circle { position(node.position)
                    radius = 10.0
                    color = RenderPalette.ForeColors[colorIdx % RenderPalette.ForeColors.size]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                        RenderNavigation.updateRoomText(node.description)
                    }
                }
            }
        }
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeRoomsOrphanAdoptingDiff(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        graphics {

            RenderNodeRooms.textView = text(text = "click a node to get uuid", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)

            RenderNavigation.roomView = text(text = "current room", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 70)

            val orphanAdoptOffset = Point(0, 400)

            val centroidMesh = INodeMesh.buildRoomMesh(Point(100, 200), height = 2)

            //           println("centroidMesh:$centroidMesh")

            for (nodeLine in centroidMesh.getNodeLineList()) {

                stroke(RenderPalette.BackColors[2], StrokeInfo(thickness = 3.0)) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

            centroidMesh.nodes.forEach { node ->

                circle { position(node.position)
                    radius = 5.0
                    color = RenderPalette.ForeColors[2]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                        RenderNavigation.updateRoomText(node.description)
                    }
                }
            }

            val roomMesh = INodeMesh.buildCentroidRoomMesh(
                height = 3,
                centroids = centroidMesh.nodes.moveNodes(Point(412, 112)).scaleNodes(scale = 1.5)
            )

            for (nodeLine in roomMesh.getNodeLineList()) {

                stroke(RenderPalette.BackColors[1], StrokeInfo(thickness = 3.0)) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

//            println("drawing nodes")
            roomMesh.nodes.forEach { node ->

                //      println(node.description)

                val numberRegex = Regex("\\d+")

                val colorIdx = numberRegex.find(node.description, 0)?.value?.toInt() ?: 0

                circle { position(node.position)
                    radius = 5.0
                    color = RenderPalette.ForeColors[colorIdx % RenderPalette.ForeColors.size]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                        RenderNavigation.updateRoomText(node.description)
                    }
                }
            }

            roomMesh.centroids.forEach { node ->

                //      println(node.description)

                val numberRegex = Regex("\\d+")

                val colorIdx = numberRegex.find(node.description, 0)?.value?.toInt() ?: 0

                circle { position(node.position)
                    radius = 10.0
                    color = RenderPalette.ForeColors[colorIdx % RenderPalette.ForeColors.size]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                        RenderNavigation.updateRoomText(node.description)
                    }
                }
            }

            roomMesh.adoptRoomOrphans()

            for (nodeLine in roomMesh.getNodeLineList()) {

                stroke(RenderPalette.BackColors[3], StrokeInfo(thickness = 3.0)) {
                    line(nodeLine!!.first + orphanAdoptOffset, nodeLine.second + orphanAdoptOffset )
                }
            }

            roomMesh.nodes.forEach { node ->

                //      println(node.description)

                val numberRegex = Regex("\\d+")

                val colorIdx = numberRegex.find(node.description, 0)?.value?.toInt() ?: 0

                circle { position(node.position + orphanAdoptOffset)
                    radius = 5.0
                    color = RenderPalette.ForeColors[colorIdx % RenderPalette.ForeColors.size]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                        RenderNavigation.updateRoomText(node.description)
                    }
                }
            }

            roomMesh.centroids.forEach { node ->

                //      println(node.description)

                val numberRegex = Regex("\\d+")

                val colorIdx = numberRegex.find(node.description, 0)?.value?.toInt() ?: 0

                circle { position(node.position + orphanAdoptOffset)
                    radius = 10.0
                    color = RenderPalette.ForeColors[colorIdx % RenderPalette.ForeColors.size]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                        RenderNavigation.updateRoomText(node.description)
                    }
                }
            }
        }
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeRoomsElaboration(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        val startingMap = mapOf(
            90 to Point(450, 600)
            , 330 to Point(300, 400)
            , 210 to Point(600, 400)
        )
        graphics {

            textView = text(text = "click a node to get uuid", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)

            val leafFirst = Leaf(topHeight = 7, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(topHeight = 7, position = startingMap[210]!!, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(topHeight = 7, position = startingMap[330]!!, angleFromParent = Angle.fromDegrees(330) )

            val threeLeaf = leafFirst.getList().plus(leafSecond.getList()).plus(leafThird.getList())
            val nodeMesh = threeLeaf.nodeMesh()
            val nodeClusters = nodeMesh.getClusters(rooms = 14, maxIterations = 7)

            var colorIdx = 0

            nodeMesh.consolidateNearNodes()

            nodeMesh.linkNearNodes()

            val allRooms = NodeMesh()

            nodeClusters.values.forEachIndexed { clusterIdx, clusterNodes -> allRooms.addMesh(NodeMesh("room$clusterIdx", clusterNodes)) }

            allRooms.consolidateNearNodes()

            allRooms.linkNearNodes()

            (1..5).forEach {

                stroke(Colors["#0f0f28"], StrokeInfo(thickness = 3.0)) {

                    for (nodeLine in allRooms.getNodeLineList() ) {
                        line(nodeLine!!.first, nodeLine.second )
                    }
                }

                stroke(Colors["#151540"], StrokeInfo(thickness = 3.0)) {

                    for (node in allRooms.nodes) {
                        circle(node.position, radius = 5.0)
                    }
                }

                val centroid = Node(position = allRooms.nodes.averagePositionWithinNodes())

                stroke(Colors["#3939ad"], StrokeInfo(thickness = 3.0)) {
                    circle(centroid.position, radius = 5.0)
                }

                val farthestNode = allRooms.nodes.getFarthestNode(centroid)

                val outerNodes60 = allRooms.nodes.nearestNodesOrderedAsc(centroid).filter { node -> Point.distance(centroid.position, node.position) >= Point.distance(centroid.position, farthestNode.position) * .5 }

                stroke(Colors["#56f636"], StrokeInfo(thickness = 3.0)) {

                    for (node in outerNodes60) {
                        circle(node.position, radius = 5.0)
                    }
                }

                val randomOuter60Node = outerNodes60.getRandomNode()

                stroke(Colors["#f65862"], StrokeInfo(thickness = 3.0)) {

                    circle(randomOuter60Node.position, radius = 5.0)
                }

                delay(3000)

                println("elaboration position: ${randomOuter60Node.position}")

                println("elaboration angle: ${centroid.angleBetween(randomOuter60Node)}")

                val newMesh = Leaf(topHeight = 5, position = randomOuter60Node.position, angleFromParent = centroid.angleBetween(randomOuter60Node) ).getList().nodeMesh()

                newMesh.consolidateNearNodes()

                newMesh.linkNearNodes()

                allRooms.addMesh(newMesh)

            }
        }
    }*/
}