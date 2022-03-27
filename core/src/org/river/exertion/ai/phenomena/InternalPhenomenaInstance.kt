package org.river.exertion.ai.phenomena

import com.badlogic.gdx.math.Vector3

class InternalPhenomenaInstance {

    var origin = Vector3(0f, 0f, 0f)
    var arising = Vector3(0f, 0f, 0f)

    var loss = 0f

    fun magnitude() = origin.dst(arising)

    fun impression() : InternalPhenomenaImpression {
        return InternalPhenomenaImpression().apply {
            origin = origin
            arising = arising
            change = Vector3(arising.x - origin.x, arising.y - origin.y,arising.z - origin.z)
            perceivedMagnitude = magnitude()

            countdown = 10f
        }
    }
}