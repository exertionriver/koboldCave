package org.river.exertion.s2d.actor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import org.river.exertion.RenderPalette
import org.river.exertion.ShapeDrawerConfig
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.geom.node.Node
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh

class ActorCave(initName : String = "Cave", initNodeRoomMesh : NodeRoomMesh) : Actor(), IBaseActor {

    override var actorName: String = initName
    var nodeRoomMesh = initNodeRoomMesh
    override var currentPosition = nodeRoomMesh.nodeRooms.first().centroid.position
    override var currentAngle = 90f
    var currentNode = Node()

    init {
        name = initName
        MessageChannel.CURNODE_BRIDGE.enableReceive(this)
        MessageChannel.NODEROOMMESH_BRIDGE.enableReceive(this)
    }

    override fun draw(batch : Batch, parentAlpha : Float) {
        render(batch, nodeRoomMesh)
    }

    override fun act(delta: Float) {
        val iter: Iterator<Action> = actions.iterator()
        while (iter.hasNext()) {
            iter.next().act(delta)
        }
    }

    override fun handleMessage(msg: Telegram?): Boolean {

        if (msg != null) {
            //           Gdx.app.log("message","actor $actorName received telegram:${msg.message}, ${(msg.sender as MessageComponent).entityName}, ${msg.extraInfo}")
            val actionMoveComponent : ActionMoveComponent = MessageChannel.CURNODE_BRIDGE.receiveMessage(msg.extraInfo)

            //receive currentNode
            if (msg.message == MessageChannel.CURNODE_BRIDGE.id() ) {
                        Gdx.app.log("message","update currentPosition to: ${actionMoveComponent.currentPosition}, currentNode to: ${actionMoveComponent.currentNode}")

                currentNode = actionMoveComponent.currentNode
                currentPosition = actionMoveComponent.currentPosition
            }

            //receive nodeMesh
            if (msg.message == MessageChannel.NODEROOMMESH_BRIDGE.id() ) {
                               Gdx.app.log("message","update nodeRoomMesh to: ${actionMoveComponent.nodeRoomMesh}")

                nodeRoomMesh = actionMoveComponent.nodeRoomMesh
            }

            MessageChannel.S2D_ECS_BRIDGE.send(this,"ping")

            return true
        }

        return false
    }

    companion object {
        fun render(batch: Batch, nodeRoomMesh : NodeRoomMesh) {

            val currentWallColor = RenderPalette.BackColors[1]
            val currentFloorColor = RenderPalette.FadeForeColors[4]
            val pastFloorColor = RenderPalette.FadeBackColors[4]
            val currentStairsColor = RenderPalette.FadeForeColors[1]
            val pastColor = RenderPalette.FadeBackColors[1]

            val sdc = ShapeDrawerConfig(batch)
            val drawer = sdc.getDrawer()

            nodeRoomMesh.nodeRooms.forEach {
                it.getExitNodes().forEachIndexed { index, exitNode ->
                    drawer.filledCircle(exitNode.position, 4F, RenderPalette.ForeColors[1])
                }
            }

            nodeRoomMesh.pastPath.entries.forEach { pathPoint ->
                val baseRadius = 0.3f
                val obsRadius = nodeRoomMesh.obstaclePath[pathPoint.key] ?: 0f
                val radius = baseRadius + obsRadius
//            val eleRadius = (this.elevationPath[pathPoint.key] ?: 0.5f) - 0.25f
                drawer.filledCircle(pathPoint.value, radius, pastFloorColor)
            }

            nodeRoomMesh.currentPath.entries.forEach { pathPoint ->
                val baseRadius = 0.3f
                val obsRadius = nodeRoomMesh.obstaclePath[pathPoint.key] ?: 0f
                val radius = baseRadius + obsRadius

                val color = nodeRoomMesh.colorPath[pathPoint.key] ?: pastFloorColor

//            println(this.elevationPath[pathPoint.key].toString() + ", " + color)
                drawer.filledCircle(pathPoint.value, radius, color)
            }

            nodeRoomMesh.pastWall.values.forEach { wallNode ->
                drawer.filledCircle(wallNode, 0.5F, pastColor)
            }

            nodeRoomMesh.currentWall.values.forEach { wallPoint ->
                drawer.filledCircle(wallPoint, 0.5F, currentWallColor)
            }

            nodeRoomMesh.pastWallFade.values.forEach { wallNode ->
                drawer.filledCircle(wallNode, 0.3F, pastColor)
            }

            nodeRoomMesh.currentWallFade.values.forEach { wallFadePoint ->
                drawer.filledCircle(wallFadePoint, 0.3F, currentWallColor)
            }

            sdc.disposeShapeDrawerConfig()
        }
    }
}