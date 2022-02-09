package org.river.exertion.koboldCave.screen

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector3
import org.river.exertion.*
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh

object Render {

    val cameraAngle = 90f

    fun initRender(camera: OrthographicCamera, currentNode: Node, currentAngle: Angle) {
        camera.position.set(currentNode.position.x, currentNode.position.y, 0f)
        camera.zoom = .2f

        val angleToRotate = cameraAngle.leftAngleBetween(currentAngle)
        camera.rotate(Vector3.Z, angleToRotate)
    }
}

