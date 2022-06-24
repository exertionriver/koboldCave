package org.river.exertion

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.Vector3
import org.river.exertion.geom.node.Node

object Render {

    val cameraAngle = 90f

    fun initRender(camera: OrthographicCamera) {
        camera.position.set(300f, 300f, 0f)
        camera.zoom = .8f
    }

    fun initRender(camera: OrthographicCamera, currentNode: Node, currentAngle: Angle) {
        camera.position.set(currentNode.position.x, currentNode.position.y, 0f)
        camera.zoom = .8f

        val angleToRotate = cameraAngle.leftAngleBetween(currentAngle)
        camera.rotate(Vector3.Z, angleToRotate)
    }

    fun initRender(camera: PerspectiveCamera, position: Vector3, lookAt : Vector3) {
        camera.fieldOfView = 30f
        camera.position.set(position)
        camera.lookAt(lookAt)
        camera.near = 0.1f
        camera.far = 500f
    }
}

