package render

import com.soywiz.klock.TimeSpan
import com.soywiz.korev.Key
import com.soywiz.korge.Korge
import com.soywiz.korge.input.keys
import com.soywiz.korge.input.onClick
import com.soywiz.korge.resources.resourceBitmap
import com.soywiz.korge.view.*
import com.soywiz.korge.view.tween.moveBy
import com.soywiz.korge.view.tween.moveTo
import com.soywiz.korge.view.tween.rotateBy
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.async.delay
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.resources.Resources
import com.soywiz.korio.resources.ResourcesContainer
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.line
import exploreKeys
import leaf.ILeaf
import leaf.ILeaf.Companion.NextDistancePx
import leaf.ILeaf.Companion.nodeMesh
import leaf.Leaf
import node.INodeMesh
import node.INodeMesh.Companion.addMesh
import node.Node
import node.Node.Companion.angleBetween
import node.Node.Companion.averagePositionWithinNodes
import node.Node.Companion.getFarthestNode
import node.Node.Companion.nearestNodesOrderedAsc
import node.NodeMesh

object RenderNodeRoomNavigation {

    @ExperimentalUnsignedTypes
    suspend fun renderNodeRoomNavigation(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        var funIdx = 0
        val funSize = 1

        while ( (funIdx >= 0) && (funIdx < funSize) ) {
//            println ("funMapIdx : $funIdx")
            commandViews[CommandView.NODE_UUID_TEXT].setText(CommandView.NODE_UUID_TEXT.label())
            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(CommandView.NODE_DESCRIPTION_TEXT.label())

            when (funIdx) {
                0 -> if ( renderNavigationRoomsStatic(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
// request out to KorGE discord on best way to arbitrary-scale / rotate container / camera example
//                1 -> if ( renderNavigationRoomsDynamic(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
//                2 -> if ( renderNavigationElaboratingRoomsDynamic(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
//                3 -> if ( renderNavigationElaboratingRoomsViewport(renderContainer, commandViews) == ButtonCommand.PREV ) funIdx--
            }
        }

        return if (funIdx > 0) ButtonCommand.NEXT else ButtonCommand.PREV
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNavigationRoomsStatic(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        val arrowPng = resourcesVfs["brightarrow.png"].readBitmap()

        commandViews[CommandView.LABEL_TEXT].setText("renderNavigationRoomsStatic() [v0.5]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing simple navigation with nodemesh of generated rooms")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("")
        commandViews[CommandView.PREV_BUTTON]!!.visible = true
        commandViews[CommandView.NEXT_BUTTON]!!.visible = true

        RenderPalette.returnClick = null

        lateinit var arrowImage : Image

        lateinit var forwardNextNodeAngle : Pair<Node, Angle>
        lateinit var backwardNextNodeAngle : Pair<Node, Angle>

        var leftNextAngle : Angle = Angle.fromDegrees(0)
        var rightNextAngle : Angle = Angle.fromDegrees(0)

        val meshPosition = Point(512, 512)
        val textOffsetPosition = Point (0, -400)

        val meshesHeight = 7

        val roomMesh = INodeMesh.buildRoomMesh(centerPoint = meshPosition, height = meshesHeight)

        var currentNode = roomMesh.getRandomNode()
        var currentAngle = roomMesh.getRandomNextNodeAngle(currentNode)

        commandViews[CommandView.NODE_UUID_TEXT].setText(currentNode.uuid.toString())
        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(currentNode.description)
        commandViews[CommandView.NODE_POSITION_TEXT].setText("${currentNode.position} > ${currentAngle.degrees}")

        val secondContainer = renderContainer.container()
        val thirdContainer = renderContainer.container()

        secondContainer.graphics {

            arrowImage = secondContainer.image(arrowPng) {
                anchor(.5, .5)
                scale(.1)
                position(currentNode.position.x, currentNode.position.y)
            }.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

            arrowImage.position(currentNode.position)
            arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

            thirdContainer.text(
                text = "NodeMesh Static Navigation",
                color = RenderPalette.ForeColors[0],
                alignment = RenderPalette.TextAlignCenter
            ).position(meshPosition + textOffsetPosition)

            val rooms = roomMesh.nodes.size / 8
            val maxIter = roomMesh.nodes.size / 6

            val roomClusters = roomMesh.getClusters(rooms = rooms, maxIterations = maxIter)

            val renderPoints =
                if (roomClusters.isNotEmpty()) roomClusters.values.flatten() else roomMesh.nodes

            //render nodeMesh rooms based upon scaledMesh centroids
            stroke(RenderPalette.BackColors[1], StrokeInfo(thickness = 3.0)) {

                for (meshLine in roomMesh.getNodeLineList()) {
                    if (meshLine != null) line(meshLine.first, meshLine.second)
                }
            }

            for (meshNode in renderPoints) {
                //https://stackoverflow.com/questions/5320525/regular-expression-to-match-last-number-in-a-string
                val numberRegex = Regex("(\\d+)(?!.*\\d)")

                val colorIdx = numberRegex.find(meshNode.description, 0)?.value?.toInt() ?: 0

                secondContainer.circle {
                    position(meshNode.position)
                    radius = 5.0
                    color = RenderPalette.ForeColors[colorIdx % RenderPalette.ForeColors.size]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(meshNode.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(meshNode.description)
                        commandViews[CommandView.NODE_POSITION_TEXT].setText(meshNode.position.toString())
                    }
                }
            }

            //            println("checking forward nodeAngle:")
            forwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, currentAngle)
//            println("checking backward nodeAngle:")
            backwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
//            println("checking leftward angle:")
            leftNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
//            println("checking rightward angle:")
            rightNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )

            keys {
                down(Key.RIGHT) {
                    currentAngle = rightNextAngle

                    commandViews[CommandView.NODE_UUID_TEXT].setText(currentNode.uuid.toString())
                    commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(currentNode.description)
                    commandViews[CommandView.NODE_POSITION_TEXT].setText("${currentNode.position} > ${currentAngle.degrees}")


                    arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

    //                println("checking forward nodeAngle:")
                    forwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, currentAngle)
    //                println("checking backward nodeAngle:")
                    backwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
    //                println("checking leftward angle:")
                    leftNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
    //                println("checking rightward angle:")
                    rightNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )
                }
                down(Key.UP) {
                    currentNode = forwardNextNodeAngle.first
                    currentAngle = forwardNextNodeAngle.second

                    commandViews[CommandView.NODE_UUID_TEXT].setText(currentNode.uuid.toString())
                    commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(currentNode.description)
                    commandViews[CommandView.NODE_POSITION_TEXT].setText("${currentNode.position} > ${currentAngle.degrees}")

                    arrowImage.position(currentNode.position)
                    arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

    //                println("checking forward nodeAngle:")
                    forwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, currentAngle)
    //                println("checking backward nodeAngle:")
                    backwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
    //                println("checking leftward angle:")
                    leftNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
    //                println("checking rightward angle:")
                    rightNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )
                }
                down(Key.LEFT) {
                    currentAngle = leftNextAngle

                    commandViews[CommandView.NODE_UUID_TEXT].setText(currentNode.uuid.toString())
                    commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(currentNode.description)
                    commandViews[CommandView.NODE_POSITION_TEXT].setText("${currentNode.position} > ${currentAngle.degrees}")

                    arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

    //                println("checking forward nodeAngle:")
                    forwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, currentAngle)
    //                println("checking backward nodeAngle:")
                    backwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
    //                println("checking leftward angle:")
                    leftNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
    //                println("checking rightward angle:")
                    rightNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )

                }
                down(Key.DOWN) {
                    //move 'backwardmost' direction. Stop if nothing within 120 degrees backward

                    currentNode = backwardNextNodeAngle.first
                    currentAngle = (Angle.fromDegrees(180) + backwardNextNodeAngle.second).normalized

                    commandViews[CommandView.NODE_UUID_TEXT].setText(currentNode.uuid.toString())
                    commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(currentNode.description)
                    commandViews[CommandView.NODE_POSITION_TEXT].setText("${currentNode.position} > ${currentAngle.degrees}")


                    arrowImage.position(currentNode.position)
                    arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

    //                println("checking forward nodeAngle:")
                    forwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, currentAngle)
    //                println("checking backward nodeAngle:")
                    backwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
    //                println("checking leftward angle:")
                    leftNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
    //                println("checking rightward angle:")
                    rightNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )
                }
            }
        }
        secondContainer.exploreKeys()

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }


    @ExperimentalUnsignedTypes
    suspend fun renderNavigationRoomsDynamic(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        val arrowPng = resourcesVfs["brightarrow.png"].readBitmap()

        commandViews[CommandView.LABEL_TEXT].setText("renderNavigationRoomsDynamic() [v0.5]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing dynamic navigation with nodemesh of generated rooms")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("")
        commandViews[CommandView.PREV_BUTTON]!!.visible = true
        commandViews[CommandView.NEXT_BUTTON]!!.visible = true

        RenderPalette.returnClick = null

        lateinit var arrowImage : Image

        lateinit var forwardNextNodeAngle : Pair<Node, Angle>
        lateinit var backwardNextNodeAngle : Pair<Node, Angle>

        var leftNextAngle : Angle = Angle.fromDegrees(0)
        var rightNextAngle : Angle = Angle.fromDegrees(0)

        val meshPosition = Point(512, 512)
        val textOffsetPosition = Point (0, -400)

        val meshesHeight = 7

        val roomMesh = INodeMesh.buildRoomMesh(centerPoint = meshPosition, height = meshesHeight)

        var currentNode = roomMesh.getRandomNode()
        var currentAngle = roomMesh.getRandomNextNodeAngle(currentNode)

        commandViews[CommandView.NODE_UUID_TEXT].setText(currentNode.uuid.toString())
        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(currentNode.description)
        commandViews[CommandView.NODE_POSITION_TEXT].setText("${currentNode.position} > ${currentAngle.degrees}")

        val secondContainer = renderContainer.container()
        val thirdContainer = renderContainer.container()

        secondContainer.graphics {

            arrowImage = secondContainer.image(arrowPng) {
                anchor(.5, .5)
                scale(.1)
                position(currentNode.position.x, currentNode.position.y)
            }.rotation(Angle.fromDegrees(90))

            arrowImage.position(currentNode.position)

            thirdContainer.text(
                text = "NodeMesh Dynamic Navigation",
                color = RenderPalette.ForeColors[0],
                alignment = RenderPalette.TextAlignCenter
            ).position(meshPosition + textOffsetPosition)

            val rooms = roomMesh.nodes.size / 8
            val maxIter = roomMesh.nodes.size / 6

            val roomClusters = roomMesh.getClusters(rooms = rooms, maxIterations = maxIter)

            val renderPoints =
                if (roomClusters.isNotEmpty()) roomClusters.values.flatten() else roomMesh.nodes

            //render nodeMesh rooms based upon scaledMesh centroids
            stroke(RenderPalette.BackColors[1], StrokeInfo(thickness = 3.0)) {

                for (meshLine in roomMesh.getNodeLineList()) {
                    if (meshLine != null) line(meshLine.first, meshLine.second)
                }
            }

            for (meshNode in renderPoints) {
                //https://stackoverflow.com/questions/5320525/regular-expression-to-match-last-number-in-a-string
                val numberRegex = Regex("(\\d+)(?!.*\\d)")

                val colorIdx = numberRegex.find(meshNode.description, 0)?.value?.toInt() ?: 0

                secondContainer.circle {
                    position(meshNode.position)
                    radius = 5.0
                    color = RenderPalette.ForeColors[colorIdx % RenderPalette.ForeColors.size]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(meshNode.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(meshNode.description)
                        commandViews[CommandView.NODE_POSITION_TEXT].setText(meshNode.position.toString())
                    }
                }
            }
            println("before, secondContainer.pos: ${secondContainer.pos}")
//            secondContainer.pos = currentNode.position - meshPosition
            secondContainer.pos = Point(-meshPosition.x, -meshPosition.y)
            secondContainer.invalidateMatrix()
            secondContainer.rotateBy(Angle.fromDegrees(40))
            secondContainer.moveTo(-meshPosition.x+10, -meshPosition.y+10)
            println("after, secondContainer.pos: ${secondContainer.pos}")
//            secondContainer.rotateBy(Angle.fromDegrees(90 - currentAngle.degrees))

            //            println("checking forward nodeAngle:")
            forwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, currentAngle)
//            println("checking backward nodeAngle:")
            backwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
//            println("checking leftward angle:")
            leftNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
//            println("checking rightward angle:")
            rightNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )

            keys {
                down(Key.RIGHT) {
                    secondContainer.pos = currentNode.position - meshPosition
             //       secondContainer.rotateBy(currentAngle - rightNextAngle)
                    println("right, secondContainer.pos: ${secondContainer.pos}")

                    currentAngle = rightNextAngle

                    commandViews[CommandView.NODE_UUID_TEXT].setText(currentNode.uuid.toString())
                    commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(currentNode.description)
                    commandViews[CommandView.NODE_POSITION_TEXT].setText("${currentNode.position} > ${currentAngle.degrees}")

//                    arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

                    //                println("checking forward nodeAngle:")
                    forwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, currentAngle)
                    //                println("checking backward nodeAngle:")
                    backwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
                    //                println("checking leftward angle:")
                    leftNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
                    //                println("checking rightward angle:")
                    rightNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )
                }
                down(Key.UP) {
                    secondContainer.pos = currentNode.position - meshPosition
                    secondContainer.moveBy(currentNode.position.x - forwardNextNodeAngle.first.position.x, currentNode.position.y - forwardNextNodeAngle.first.position.y )
                    println("up, secondContainer.pos: ${secondContainer.pos}")

                    currentNode = forwardNextNodeAngle.first
                    currentAngle = forwardNextNodeAngle.second

                    commandViews[CommandView.NODE_UUID_TEXT].setText(currentNode.uuid.toString())
                    commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(currentNode.description)
                    commandViews[CommandView.NODE_POSITION_TEXT].setText("${currentNode.position} > ${currentAngle.degrees}")

                    arrowImage.position(currentNode.position)
 //                   arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

                    //                println("checking forward nodeAngle:")
                    forwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, currentAngle)
                    //                println("checking backward nodeAngle:")
                    backwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
                    //                println("checking leftward angle:")
                    leftNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
                    //                println("checking rightward angle:")
                    rightNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )
                }
                down(Key.LEFT) {
                    secondContainer.pos = currentNode.position - meshPosition
            //        secondContainer.rotateBy(currentAngle - leftNextAngle)
                    println("left, secondContainer.pos: ${secondContainer.pos}")

                    currentAngle = leftNextAngle

                    commandViews[CommandView.NODE_UUID_TEXT].setText(currentNode.uuid.toString())
                    commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(currentNode.description)
                    commandViews[CommandView.NODE_POSITION_TEXT].setText("${currentNode.position} > ${currentAngle.degrees}")

//                    arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

                    //                println("checking forward nodeAngle:")
                    forwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, currentAngle)
                    //                println("checking backward nodeAngle:")
                    backwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
                    //                println("checking leftward angle:")
                    leftNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
                    //                println("checking rightward angle:")
                    rightNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )

                }
                down(Key.DOWN) {
                    //move 'backwardmost' direction. Stop if nothing within 120 degrees backward
                    secondContainer.pos = currentNode.position - meshPosition
                    secondContainer.moveBy(currentNode.position.x - backwardNextNodeAngle.first.position.x, currentNode.position.y - backwardNextNodeAngle.first.position.y )
                    println("down, secondContainer.pos: ${secondContainer.pos}")

                    currentNode = backwardNextNodeAngle.first
                    currentAngle = (Angle.fromDegrees(180) + backwardNextNodeAngle.second).normalized

                    commandViews[CommandView.NODE_UUID_TEXT].setText(currentNode.uuid.toString())
                    commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(currentNode.description)
                    commandViews[CommandView.NODE_POSITION_TEXT].setText("${currentNode.position} > ${currentAngle.degrees}")


                    arrowImage.position(currentNode.position)
    //                arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

                    //                println("checking forward nodeAngle:")
                    forwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, currentAngle)
                    //                println("checking backward nodeAngle:")
                    backwardNextNodeAngle = roomMesh.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
                    //                println("checking leftward angle:")
                    leftNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
                    //                println("checking rightward angle:")
                    rightNextAngle = roomMesh.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )
                }
            }
        }
        secondContainer.exploreKeys()

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNavigationElaboratingRoomsDynamic(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderNavigationElaboratingRoomsDynamic() [v0.5]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing INodeMesh.adoptRoomOrphans() with INodeMesh.buildRoomMesh() generated rooms")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("text console displays orphaned node adoptions between rooms (work in progress)")
        commandViews[CommandView.PREV_BUTTON]!!.visible = true
        commandViews[CommandView.NEXT_BUTTON]!!.visible = false

        RenderPalette.returnClick = null

        val secondContainer = renderContainer.container()
        secondContainer.graphics {
/*
        val roomColors = listOf(
            Colors.DARKRED, Colors.DARKGREEN,  Colors.BLUE, Colors.DARKMAGENTA, Colors.DARKSEAGREEN, Colors.DARKTURQUOISE
            , Colors.DARKORANGE, Colors.DARKOLIVEGREEN, Colors.DARKSALMON)

        val roomPathColor = Colors["#427d7a"]

        val startingMap = mapOf(
            90 to Point(450, 600)
            , 330 to Point(300, 400)
            , 210 to Point(600, 400)
        )

        var currentNode = Node()
        var currentAngle = Angle.fromDegrees(0)
        lateinit var forwardNextNodeAngle : Pair<Node, Angle>
        lateinit var backwardNextNodeAngle : Pair<Node, Angle>
        var leftNextAngle : Angle = Angle.fromDegrees(0)
        var rightNextAngle : Angle = Angle.fromDegrees(0)

        val renderNodeMap : MutableMap<Node, View> = mutableMapOf()
        val allRooms = NodeMesh(description = "allRooms")
        lateinit var arrowImage : Image

        var centroid = Node()
        var farthestNode = Node()
        var outerNodes60 : List<Node> = listOf()


        graphics {

            nodeView = text(text = "current node", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)
            angleView = text(text = "current node angle", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 45)
            roomView = text(text = "current room", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 70)

            val leafFirst = Leaf(topHeight = 6, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(topHeight = 6, position = startingMap[210]!!, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(topHeight = 6, position = startingMap[330]!!, angleFromParent = Angle.fromDegrees(330) )

            val nodeMesh = leafFirst.getList().plus(leafSecond.getList()).plus(leafThird.getList()).nodeMesh()
            val nodeClusters = nodeMesh.getClusters(rooms = nodeMesh.nodes.size / 20, maxIterations = 7)
            var colorIdx = 0

            nodeClusters.values.forEachIndexed { clusterIdx, clusterNodes -> allRooms.addMesh(NodeMesh("room$clusterIdx", clusterNodes), description = "room$clusterIdx") }

            allRooms.consolidateNodeLinks()

            allRooms.consolidateNearNodes()

            allRooms.linkNearNodes()

            stroke(Colors["#0f0f28"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in allRooms.getNodeLineList() ) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

            stroke(Colors["#151540"], StrokeInfo(thickness = 3.0)) {

                for (node in allRooms.nodes) {
                    renderNodeMap[node] = container.circle(radius = 5.0).position(node.position)
                }
            }

            nodeClusters.forEach { nodeCluster ->
                val nodeRoom = NodeMesh(linkNodes = nodeCluster.value)

                nodeRoom.consolidateNearNodes()

                nodeRoom.linkNearNodes()

                stroke(roomColors[colorIdx % 9], StrokeInfo(thickness = 2.0)) {

                    for (nodeLine in nodeRoom.getNodeLineList()) {
                        line(nodeLine!!.first, nodeLine.second)
                    }
                }

                nodeRoom.nodes.forEach { node ->
                    circle { position(node.position)
                        radius = 2.0
                        color = roomColors[colorIdx % 9]
                        strokeThickness = 3.0
                        onClick{ updateNodeText(node.uuid.toString())
                        }
                    }
                }

                colorIdx++
            }

            println("getting random node:")
            currentNode = allRooms.getRandomNode()
            println("getting random angle:")
            currentAngle = allRooms.getRandomNextNodeAngle(currentNode)

            renderNodeMap[currentNode]!!.colorMul = Colors["#ff00ed"]

            updateNodeText(currentNode.uuid.toString())
            updateAngleText(currentAngle.degrees.toString())
            updateRoomText(currentNode.description)

            arrowImage = image(arrow_png) {
                anchor(.5, .5)
                scale(.1)
                position(currentNode.position.x, currentNode.position.y)
            }.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

   //         arrowImage.position(currentNode.position)
 //           arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

//            println("checking forward nodeAngle:")
            forwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, currentAngle)
//            println("checking backward nodeAngle:")
            backwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
//            println("checking leftward angle:")
            leftNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
//            println("checking rightward angle:")
            rightNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )

            centroid = Node(position = allRooms.nodes.averagePositionWithinNodes())
            farthestNode = allRooms.nodes.getFarthestNode(centroid)
            outerNodes60 = allRooms.nodes.nearestNodesOrderedAsc(centroid).filter { node -> Point.distance(centroid.position, node.position) >= Point.distance(centroid.position, farthestNode.position) * .5 }

        }

        keys {
            down(Key.RIGHT) {
//                container.rotateBy(currentAngle - rightNextAngle)

                currentAngle = rightNextAngle

                updateNodeText(currentNode.uuid.toString())
                updateAngleText(currentAngle.degrees.toString())
                updateRoomText(currentNode.description)

                arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

//                println("checking forward nodeAngle:")
                forwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, currentAngle)
//                println("checking backward nodeAngle:")
                backwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
//                println("checking leftward angle:")
                leftNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
//                println("checking rightward angle:")
                rightNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )
            }
            down(Key.UP) {
                container.moveBy(currentNode.position.x - forwardNextNodeAngle.first.position.x, currentNode.position.y - forwardNextNodeAngle.first.position.y )

                renderNodeMap[currentNode]!!.colorMul = Colors["#151540"]

                currentNode = forwardNextNodeAngle.first
                currentAngle = forwardNextNodeAngle.second

                renderNodeMap[currentNode]!!.colorMul = Colors["#ff00ed"]

                updateNodeText(currentNode.uuid.toString())
                updateAngleText(currentAngle.degrees.toString())
                updateRoomText(currentNode.description)

                if (outerNodes60.contains(currentNode)) {
                    val newMeshLocation = ILeaf.getChildPosition(parentPosition = currentNode.position, distanceFromParent = 3 * NextDistancePx, childAngle = centroid.angleBetween(currentNode))

                    val newMesh = INodeMesh.buildRoomMesh(newMeshLocation, height = 5)

//                    val newMesh = Leaf(initHeight = 5, position = currentNode.position, angleFromParent = centroid.angleBetween(currentNode) ).getLeafList().nodeMesh()

                    allRooms.addMesh(newMesh)
                    allRooms.consolidateNearNodes()
                    allRooms.linkNearNodes()
                    allRooms.consolidateNodeLinks()

                    graphics {
                        stroke(Colors["#0f0f28"], StrokeInfo(thickness = 3.0)) {

                            for (nodeLine in newMesh.getNodeLineList()) {
                                line(nodeLine!!.first, nodeLine.second)
                            }
                        }

                        stroke(Colors["#151540"], StrokeInfo(thickness = 3.0)) {

                            for (node in newMesh.nodes) {
                                renderNodeMap[node] = container.circle(radius = 5.0).position(node.position)
                            }
                        }
                    }
                }

                arrowImage.position(currentNode.position)
                arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

//                println("checking forward nodeAngle:")
                forwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, currentAngle)
//                println("checking backward nodeAngle:")
                backwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
//                println("checking leftward angle:")
                leftNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
//                println("checking rightward angle:")
                rightNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )

                centroid = Node(position = allRooms.nodes.averagePositionWithinNodes())
                farthestNode = allRooms.nodes.getFarthestNode(centroid)
                outerNodes60 = allRooms.nodes.nearestNodesOrderedAsc(centroid).filter { node -> Point.distance(centroid.position, node.position) >= Point.distance(centroid.position, farthestNode.position) * .5 }
            }
            down(Key.LEFT) {
//                container.rotateBy(currentAngle - leftNextAngle)

                currentAngle = leftNextAngle

                updateNodeText(currentNode.uuid.toString())
                updateAngleText(currentAngle.degrees.toString())
                updateRoomText(currentNode.description)

                arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

//                println("checking forward nodeAngle:")
                forwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, currentAngle)
//                println("checking backward nodeAngle:")
                backwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
//                println("checking leftward angle:")
                leftNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
//                println("checking rightward angle:")
                rightNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )

            }
            down(Key.DOWN) {
                container.moveBy(currentNode.position.x - backwardNextNodeAngle.first.position.x, currentNode.position.y - backwardNextNodeAngle.first.position.y )

                //move 'backwardmost' direction. Stop if nothing within 120 degrees backward
                renderNodeMap[currentNode]!!.colorMul = Colors["#151540"]

                currentNode = backwardNextNodeAngle.first
                currentAngle = (Angle.fromDegrees(180) + backwardNextNodeAngle.second).normalized

                renderNodeMap[currentNode]!!.colorMul = Colors["#ff00ed"]

                updateNodeText(currentNode.uuid.toString())
                updateAngleText(currentAngle.degrees.toString())
                updateRoomText(currentNode.description)

                if (outerNodes60.contains(currentNode)) {
                    val newMeshLocation = ILeaf.getChildPosition(parentPosition = currentNode.position, distanceFromParent = 3 * NextDistancePx, childAngle = centroid.angleBetween(currentNode))

                    val newMesh = INodeMesh.buildRoomMesh(newMeshLocation, height = 5)

                    //val newMesh = Leaf(initHeight = 5, position = currentNode.position, angleFromParent = centroid.angleBetween(currentNode) ).getLeafList().nodeMesh()

                    allRooms.addMesh(newMesh)
                    allRooms.consolidateNearNodes()
                    allRooms.linkNearNodes()
                    allRooms.consolidateNodeLinks()

                    graphics {
                        stroke(Colors["#0f0f28"], StrokeInfo(thickness = 3.0)) {

                            for (nodeLine in allRooms.getNodeLineList()) {
                                line(nodeLine!!.first, nodeLine.second)
                            }
                        }

                        stroke(Colors["#151540"], StrokeInfo(thickness = 3.0)) {

                            for (node in allRooms.nodes) {
                                renderNodeMap[node] = container.circle(radius = 5.0).position(node.position)
                            }
                        }
                    }
                }

                arrowImage.position(currentNode.position)
                arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

//                println("checking forward nodeAngle:")
                forwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, currentAngle)
//                println("checking backward nodeAngle:")
                backwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
//                println("checking leftward angle:")
                leftNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
//                println("checking rightward angle:")
                rightNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )

                centroid = Node(position = allRooms.nodes.averagePositionWithinNodes())
                farthestNode = allRooms.nodes.getFarthestNode(centroid)
                outerNodes60 = allRooms.nodes.nearestNodesOrderedAsc(centroid).filter { node -> Point.distance(centroid.position, node.position) >= Point.distance(centroid.position, farthestNode.position) * .5 }
            }
*/        }

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNavigationElaboratingRoomsViewport(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderNavigationElaboratingRooms() [v0.5]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing INodeMesh.adoptRoomOrphans() with INodeMesh.buildRoomMesh() generated rooms")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("text console displays orphaned node adoptions between rooms (work in progress)")
        commandViews[CommandView.PREV_BUTTON]!!.visible = true
        commandViews[CommandView.NEXT_BUTTON]!!.visible = false

        RenderPalette.returnClick = null

        val secondContainer = renderContainer.container()
        secondContainer.graphics {
/*
        val roomColors = listOf(
            Colors.DARKRED, Colors.DARKGREEN,  Colors.BLUE, Colors.DARKMAGENTA, Colors.DARKSEAGREEN, Colors.DARKTURQUOISE
            , Colors.DARKORANGE, Colors.DARKOLIVEGREEN, Colors.DARKSALMON)

        val roomPathColor = Colors["#427d7a"]

        val startingMap = mapOf(
            90 to Point(450, 600)
            , 330 to Point(300, 400)
            , 210 to Point(600, 400)
        )

        var currentNode = Node()
        var currentAngle = Angle.fromDegrees(0)
        lateinit var forwardNextNodeAngle : Pair<Node, Angle>
        lateinit var backwardNextNodeAngle : Pair<Node, Angle>
        var leftNextAngle : Angle = Angle.fromDegrees(0)
        var rightNextAngle : Angle = Angle.fromDegrees(0)

        val renderNodeMap : MutableMap<Node, View> = mutableMapOf()
        val allRooms = NodeMesh(description = "allRooms")
        lateinit var arrowImage : Image

        var centroid = Node()
        var farthestNode = Node()
        var outerNodes60 : List<Node> = listOf()


        graphics {

            nodeView = text(text = "current node", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)
            angleView = text(text = "current node angle", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 45)
            roomView = text(text = "current room", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 70)

            val leafFirst = Leaf(topHeight = 6, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(topHeight = 6, position = startingMap[210]!!, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(topHeight = 6, position = startingMap[330]!!, angleFromParent = Angle.fromDegrees(330) )

            val nodeMesh = leafFirst.getList().plus(leafSecond.getList()).plus(leafThird.getList()).nodeMesh()
            val nodeClusters = nodeMesh.getClusters(rooms = nodeMesh.nodes.size / 20, maxIterations = 7)
            var colorIdx = 0

            nodeClusters.values.forEachIndexed { clusterIdx, clusterNodes -> allRooms.addMesh(NodeMesh("room$clusterIdx", clusterNodes), description = "room$clusterIdx") }

            allRooms.consolidateNodeLinks()

            allRooms.consolidateNearNodes()

            allRooms.linkNearNodes()

            stroke(Colors["#0f0f28"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in allRooms.getNodeLineList() ) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

            stroke(Colors["#151540"], StrokeInfo(thickness = 3.0)) {

                for (node in allRooms.nodes) {
                    renderNodeMap[node] = container.circle(radius = 5.0).position(node.position)
                }
            }

            nodeClusters.forEach { nodeCluster ->
                val nodeRoom = NodeMesh(linkNodes = nodeCluster.value)

                nodeRoom.consolidateNearNodes()

                nodeRoom.linkNearNodes()

                stroke(roomColors[colorIdx % 9], StrokeInfo(thickness = 2.0)) {

                    for (nodeLine in nodeRoom.getNodeLineList()) {
                        line(nodeLine!!.first, nodeLine.second)
                    }
                }

                nodeRoom.nodes.forEach { node ->
                    circle { position(node.position)
                        radius = 2.0
                        color = roomColors[colorIdx % 9]
                        strokeThickness = 3.0
                        onClick{ updateNodeText(node.uuid.toString())
                        }
                    }
                }

                colorIdx++
            }

            println("getting random node:")
            currentNode = allRooms.getRandomNode()
            println("getting random angle:")
            currentAngle = allRooms.getRandomNextNodeAngle(currentNode)

            renderNodeMap[currentNode]!!.colorMul = Colors["#ff00ed"]

            updateNodeText(currentNode.uuid.toString())
            updateAngleText(currentAngle.degrees.toString())
            updateRoomText(currentNode.description)

            arrowImage = image(arrow_png) {
                anchor(.5, .5)
                scale(.1)
                position(currentNode.position.x, currentNode.position.y)
            }.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

   //         arrowImage.position(currentNode.position)
 //           arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

//            println("checking forward nodeAngle:")
            forwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, currentAngle)
//            println("checking backward nodeAngle:")
            backwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
//            println("checking leftward angle:")
            leftNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
//            println("checking rightward angle:")
            rightNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )

            centroid = Node(position = allRooms.nodes.averagePositionWithinNodes())
            farthestNode = allRooms.nodes.getFarthestNode(centroid)
            outerNodes60 = allRooms.nodes.nearestNodesOrderedAsc(centroid).filter { node -> Point.distance(centroid.position, node.position) >= Point.distance(centroid.position, farthestNode.position) * .5 }

        }

        keys {
            down(Key.RIGHT) {
//                container.rotateBy(currentAngle - rightNextAngle)

                currentAngle = rightNextAngle

                updateNodeText(currentNode.uuid.toString())
                updateAngleText(currentAngle.degrees.toString())
                updateRoomText(currentNode.description)

                arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

//                println("checking forward nodeAngle:")
                forwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, currentAngle)
//                println("checking backward nodeAngle:")
                backwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
//                println("checking leftward angle:")
                leftNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
//                println("checking rightward angle:")
                rightNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )
            }
            down(Key.UP) {
                container.moveBy(currentNode.position.x - forwardNextNodeAngle.first.position.x, currentNode.position.y - forwardNextNodeAngle.first.position.y )

                renderNodeMap[currentNode]!!.colorMul = Colors["#151540"]

                currentNode = forwardNextNodeAngle.first
                currentAngle = forwardNextNodeAngle.second

                renderNodeMap[currentNode]!!.colorMul = Colors["#ff00ed"]

                updateNodeText(currentNode.uuid.toString())
                updateAngleText(currentAngle.degrees.toString())
                updateRoomText(currentNode.description)

                if (outerNodes60.contains(currentNode)) {
                    val newMeshLocation = ILeaf.getChildPosition(parentPosition = currentNode.position, distanceFromParent = 3 * NextDistancePx, childAngle = centroid.angleBetween(currentNode))

                    val newMesh = INodeMesh.buildRoomMesh(newMeshLocation, height = 5)

//                    val newMesh = Leaf(initHeight = 5, position = currentNode.position, angleFromParent = centroid.angleBetween(currentNode) ).getLeafList().nodeMesh()

                    allRooms.addMesh(newMesh)
                    allRooms.consolidateNearNodes()
                    allRooms.linkNearNodes()
                    allRooms.consolidateNodeLinks()

                    graphics {
                        stroke(Colors["#0f0f28"], StrokeInfo(thickness = 3.0)) {

                            for (nodeLine in newMesh.getNodeLineList()) {
                                line(nodeLine!!.first, nodeLine.second)
                            }
                        }

                        stroke(Colors["#151540"], StrokeInfo(thickness = 3.0)) {

                            for (node in newMesh.nodes) {
                                renderNodeMap[node] = container.circle(radius = 5.0).position(node.position)
                            }
                        }
                    }
                }

                arrowImage.position(currentNode.position)
                arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

//                println("checking forward nodeAngle:")
                forwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, currentAngle)
//                println("checking backward nodeAngle:")
                backwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
//                println("checking leftward angle:")
                leftNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
//                println("checking rightward angle:")
                rightNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )

                centroid = Node(position = allRooms.nodes.averagePositionWithinNodes())
                farthestNode = allRooms.nodes.getFarthestNode(centroid)
                outerNodes60 = allRooms.nodes.nearestNodesOrderedAsc(centroid).filter { node -> Point.distance(centroid.position, node.position) >= Point.distance(centroid.position, farthestNode.position) * .5 }
            }
            down(Key.LEFT) {
//                container.rotateBy(currentAngle - leftNextAngle)

                currentAngle = leftNextAngle

                updateNodeText(currentNode.uuid.toString())
                updateAngleText(currentAngle.degrees.toString())
                updateRoomText(currentNode.description)

                arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

//                println("checking forward nodeAngle:")
                forwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, currentAngle)
//                println("checking backward nodeAngle:")
                backwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
//                println("checking leftward angle:")
                leftNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
//                println("checking rightward angle:")
                rightNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )

            }
            down(Key.DOWN) {
                container.moveBy(currentNode.position.x - backwardNextNodeAngle.first.position.x, currentNode.position.y - backwardNextNodeAngle.first.position.y )

                //move 'backwardmost' direction. Stop if nothing within 120 degrees backward
                renderNodeMap[currentNode]!!.colorMul = Colors["#151540"]

                currentNode = backwardNextNodeAngle.first
                currentAngle = (Angle.fromDegrees(180) + backwardNextNodeAngle.second).normalized

                renderNodeMap[currentNode]!!.colorMul = Colors["#ff00ed"]

                updateNodeText(currentNode.uuid.toString())
                updateAngleText(currentAngle.degrees.toString())
                updateRoomText(currentNode.description)

                if (outerNodes60.contains(currentNode)) {
                    val newMeshLocation = ILeaf.getChildPosition(parentPosition = currentNode.position, distanceFromParent = 3 * NextDistancePx, childAngle = centroid.angleBetween(currentNode))

                    val newMesh = INodeMesh.buildRoomMesh(newMeshLocation, height = 5)

                    //val newMesh = Leaf(initHeight = 5, position = currentNode.position, angleFromParent = centroid.angleBetween(currentNode) ).getLeafList().nodeMesh()

                    allRooms.addMesh(newMesh)
                    allRooms.consolidateNearNodes()
                    allRooms.linkNearNodes()
                    allRooms.consolidateNodeLinks()

                    graphics {
                        stroke(Colors["#0f0f28"], StrokeInfo(thickness = 3.0)) {

                            for (nodeLine in allRooms.getNodeLineList()) {
                                line(nodeLine!!.first, nodeLine.second)
                            }
                        }

                        stroke(Colors["#151540"], StrokeInfo(thickness = 3.0)) {

                            for (node in allRooms.nodes) {
                                renderNodeMap[node] = container.circle(radius = 5.0).position(node.position)
                            }
                        }
                    }
                }

                arrowImage.position(currentNode.position)
                arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

//                println("checking forward nodeAngle:")
                forwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, currentAngle)
//                println("checking backward nodeAngle:")
                backwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
//                println("checking leftward angle:")
                leftNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
//                println("checking rightward angle:")
                rightNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )

                centroid = Node(position = allRooms.nodes.averagePositionWithinNodes())
                farthestNode = allRooms.nodes.getFarthestNode(centroid)
                outerNodes60 = allRooms.nodes.nearestNodesOrderedAsc(centroid).filter { node -> Point.distance(centroid.position, node.position) >= Point.distance(centroid.position, farthestNode.position) * .5 }
            }
*/        }

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }
}