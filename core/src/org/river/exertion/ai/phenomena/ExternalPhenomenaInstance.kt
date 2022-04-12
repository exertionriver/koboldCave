package org.river.exertion.ai.phenomena

import com.badlogic.gdx.math.Vector3
import org.river.exertion.btree.v0_1.Behavior
import org.river.exertion.normalizeDeg

class ExternalPhenomenaInstance {

    var type = ExternalPhenomenaType.NONE
    var taskType = Behavior.NONE
    var location = Vector3(0f, 0f, 0f)
    var magnitude = 0f
    var direction = 0f //angle
    var effectArc = 0f //from directionAngle

    var loss = 0f //% per distance unit
    var fade = 0f //% per time unit

    fun impression() : ExternalPhenomenaImpression {
        return ExternalPhenomenaImpression().apply {
            type = this@ExternalPhenomenaInstance.type
            taskType = this@ExternalPhenomenaInstance.taskType
            perceivedDistance = 10f
            perceivedMagnitude = this@ExternalPhenomenaInstance.magnitude / 2f
            perceivedDirection = (this@ExternalPhenomenaInstance.direction + 180f).normalizeDeg()

            countdown = 10f
        }
    }
}