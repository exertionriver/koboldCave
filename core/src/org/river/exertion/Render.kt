package org.river.exertion

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import org.river.exertion.geom.node.Node

object Render {

    val cameraAngle = 90f

    fun initRender(camera: OrthographicCamera, currentNode: Node, currentAngle: Angle) {
        camera.position.set(currentNode.position.x, currentNode.position.y, 0f)
        camera.zoom = .2f

        val angleToRotate = cameraAngle.leftAngleBetween(currentAngle)
        camera.rotate(Vector3.Z, angleToRotate)
    }
}

