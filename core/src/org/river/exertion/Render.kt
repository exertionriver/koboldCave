package org.river.exertion

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.Vector3
import org.river.exertion.geom.node.Node

object Render {

    val cameraAngle = 90f

    fun initRender(camera: OrthographicCamera, currentNode: Node, currentAngle: Angle) {
        camera.position.set(currentNode.position.x, currentNode.position.y, 0f)
        camera.zoom = .8f

        val angleToRotate = cameraAngle.leftAngleBetween(currentAngle)
        camera.rotate(Vector3.Z, angleToRotate)
    }

    fun initRender(camera: PerspectiveCamera, currentNode: Node, currentAngle: Angle) {
        camera.position.set(currentNode.position.x, currentNode.position.y, 100f)
        camera.lookAt(currentNode.position.x, currentNode.position.y, 0f)
        camera.near = 0.1f
        camera.far = 500f

        val angleToRotate = cameraAngle.leftAngleBetween(currentAngle)
        camera.rotate(Vector3.Z, angleToRotate)
    }
}

