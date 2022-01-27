package org.river.exertion.koboldCave.screen

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector3
import org.river.exertion.*
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh
import org.river.exertion.koboldQueue.condition.Probability

object Render {

    val cameraAngle = 90f

    fun initRender(camera: OrthographicCamera, currentNode : Node, currentAngle : Angle) {
        camera.position.set(currentNode.position.x, currentNode.position.y, 0f)
        camera.zoom = .2f

        val angleToRotate = cameraAngle.leftAngleBetween(currentAngle)
        camera.rotate(Vector3.Z, angleToRotate)
    }
}


fun NodeRoomMesh.render(batch : Batch) {

        val currentWallColor = RenderPalette.BackColors[1]
        val currentFloorColor = RenderPalette.FadeForeColors[4]
        val pastFloorColor = RenderPalette.FadeBackColors[4]
        val currentStairsColor = RenderPalette.FadeForeColors[1]
        val pastColor = RenderPalette.FadeBackColors[1]

        val sdc = ShapeDrawerConfig(batch)
        val drawer = sdc.getDrawer()

        val arcSdc = ShapeDrawerConfig(batch, currentStairsColor)
        val arcDrawer = arcSdc.getDrawer()

        val arcPastSdc = ShapeDrawerConfig(batch, pastColor)
        val arcPastDrawer = arcPastSdc.getDrawer()
/*
        this.pastStairs.entries.forEach { stairNode ->
            arcPastDrawer.arc(stairNode.key.x, stairNode.key.y, 6F, (stairNode.value - 60f).radians(), 120f.radians() )
        }

        this.currentStairs.entries.forEach { stairNode ->
            arcDrawer.arc(stairNode.key.x, stairNode.key.y, 6F, (stairNode.value - 60f).radians(), 120f.radians() )
        }
*/
        this.pastPath.entries.forEach { pathPoint ->
            val radius = this.obstaclePath[pathPoint.key] ?: 0.25f
            drawer.filledCircle(pathPoint.value, radius, pastFloorColor)
        }

        this.currentPath.entries.forEach { pathPoint ->
            val radius = this.obstaclePath[pathPoint.key] ?: 0.25f
            drawer.filledCircle(pathPoint.value, radius, currentFloorColor)
        }

        this.pastWall.values.forEach { wallNode ->
            drawer.filledCircle(wallNode, 0.5F, pastColor)
        }

        this.currentWall.values.forEach { wallPoint ->
            drawer.filledCircle(wallPoint, 0.5F, currentWallColor)
        }

        this.pastWallFade.values.forEach { wallNode ->
            drawer.filledCircle(wallNode, 0.3F, pastColor)
        }

        this.currentWallFade.values.forEach { wallFadePoint ->
            drawer.filledCircle(wallFadePoint, 0.3F, currentWallColor)
        }

        arcSdc.disposeShapeDrawerConfig()
        arcPastSdc.disposeShapeDrawerConfig()
    }

