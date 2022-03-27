package org.river.exertion.ai.phenomena

import com.badlogic.gdx.math.Vector3

class InternalPhenomenaInstance {

    var origin = Vector3(0f, 0f, 0f)
    var arising = Vector3(0f, 0f, 0f)

    var loss = 0f

    fun magnitude() = origin.dst(arising)
    fun change() = Vector3(arising.x - origin.x, arising.y - origin.y,arising.z - origin.z)

    fun impression() : InternalPhenomenaImpression {
        return InternalPhenomenaImpression().apply {
            origin = origin
            arising = arising
            change = change()
            perceivedMagnitude = magnitude()

            countdown = 10f
        }
    }

    companion object {
        fun List<InternalPhenomenaInstance>.opinion() : Vector3 {
            var resultVector = Vector3(0f, 0f, 0f)

            this.forEach {
                val change = it.change()
                resultVector.x += change.x
                resultVector.y += change.y
                resultVector.z += change.z
            }

            if (this.isNotEmpty()) {
                resultVector.x /= this.size
                resultVector.y /= this.size
                resultVector.z /= this.size
            }

            return resultVector
        }
    }
}


