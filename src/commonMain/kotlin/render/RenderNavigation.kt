package render

import com.soywiz.korev.Key
import com.soywiz.korge.Korge
import com.soywiz.korge.input.keys
import com.soywiz.korge.input.onClick
import com.soywiz.korge.resources.resourceBitmap
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.resources.ResourcesContainer
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.line
import leaf.ILeaf
import leaf.ILeaf.Companion.nodeMesh
import leaf.Leaf
import node.INodeMesh.Companion.addMesh
import node.Node
import node.NodeMesh
import render.RenderArrow.arrow_png


object RenderNavigation {

    val ResourcesContainer.arrow_png by resourceBitmap("brightarrow.png")

    lateinit var nodeView : View
    lateinit var angleView : View

    fun updateNodeText(uuidString : String) {
        nodeView.setText(uuidString)
    }

    fun updateAngleText(angleDegrees : String) {
        angleView.setText(angleDegrees)
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNavigation() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val container = this.containerRoot

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
        val allRooms = NodeMesh()
        lateinit var arrowImage : Image

        graphics {

            nodeView = text(text = "current node", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)
            angleView = text(text = "current node angle", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 45)

            val leafFirst = Leaf(initHeight = 6, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(initHeight = 6, position = startingMap[210]!!, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(initHeight = 6, position = startingMap[330]!!, angleFromParent = Angle.fromDegrees(330) )

            val nodeMesh = leafFirst.getLeafList().plus(leafSecond.getLeafList()).plus(leafThird.getLeafList()).nodeMesh()
            val nodeClusters = nodeMesh.getClusters(rooms = nodeMesh.nodes.size / 20, maxIterations = 7)
            var colorIdx = 0

            nodeClusters.values.forEachIndexed { clusterIdx, clusterNodes -> allRooms.addMesh(NodeMesh("room$clusterIdx", clusterNodes)) }

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

            arrowImage = image(arrow_png) {
                anchor(1, 1)
                scale(.1)
                position(currentNode.position.x, currentNode.position.y)
            }.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

            arrowImage.position(currentNode.position)
            arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

            println("checking forward nodeAngle:")
            forwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, currentAngle)
            println("checking backward nodeAngle:")
            backwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
            println("checking leftward angle:")
            leftNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
            println("checking rightward angle:")
            rightNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )
        }

        keys {
            down(Key.RIGHT) {
                currentAngle = rightNextAngle

                updateAngleText(currentAngle.degrees.toString())

                arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

                println("checking forward nodeAngle:")
                forwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, currentAngle)
                println("checking backward nodeAngle:")
                backwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
                println("checking leftward angle:")
                leftNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
                println("checking rightward angle:")
                rightNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )
            }
            down(Key.UP) {
                renderNodeMap[currentNode]!!.colorMul = Colors["#151540"]

                currentNode = forwardNextNodeAngle.first
                currentAngle = forwardNextNodeAngle.second

                renderNodeMap[currentNode]!!.colorMul = Colors["#ff00ed"]

                updateNodeText(currentNode.uuid.toString())
                updateAngleText(currentAngle.degrees.toString())

                arrowImage.position(currentNode.position)
                arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

                println("checking forward nodeAngle:")
                forwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, currentAngle)
                println("checking backward nodeAngle:")
                backwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
                println("checking leftward angle:")
                leftNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
                println("checking rightward angle:")
                rightNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )
            }
            down(Key.LEFT) {
                currentAngle = leftNextAngle

                updateAngleText(currentAngle.degrees.toString())

                arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

                println("checking forward nodeAngle:")
                forwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, currentAngle)
                println("checking backward nodeAngle:")
                backwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
                println("checking leftward angle:")
                leftNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
                println("checking rightward angle:")
                rightNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )

            }
            down(Key.DOWN) {
                //move 'backwardmost' direction. Stop if nothing within 120 degrees backward
                renderNodeMap[currentNode]!!.colorMul = Colors["#151540"]

                currentNode = backwardNextNodeAngle.first
                currentAngle = (Angle.fromDegrees(180) + backwardNextNodeAngle.second).normalized

                renderNodeMap[currentNode]!!.colorMul = Colors["#ff00ed"]

                updateNodeText(currentNode.uuid.toString())
                updateAngleText(currentAngle.degrees.toString())

                arrowImage.position(currentNode.position)
                arrowImage.rotation(Angle.fromDegrees(180 - currentAngle.degrees))

                println("checking forward nodeAngle:")
                forwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, currentAngle)
                println("checking backward nodeAngle:")
                backwardNextNodeAngle = allRooms.getNextNodeAngle(currentNode, (Angle.fromDegrees(180) + currentAngle).normalized)
                println("checking leftward angle:")
                leftNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(60) )
                println("checking rightward angle:")
                rightNextAngle = allRooms.getNextAngle(currentNode, currentAngle, Angle.fromDegrees(-60) )
            }
        }
    }
}