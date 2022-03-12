package org.river.exertion.geom

import com.badlogic.gdx.math.MathUtils.cos
import com.badlogic.gdx.math.MathUtils.sin
import com.badlogic.gdx.math.Vector3
import org.river.exertion.*
import kotlin.math.*

class Line3(val first : Vector3, val second: Vector3) {

    companion object {

        //https://en.wikipedia.org/wiki/Spherical_coordinate_system#Cartesian_coordinates
        //using mathematics convention
        //verified with https://planetcalc.com/7952/
        //x = distance, y = azimuth, z = polar, polar is between 0 and 180 deg
        //from spherical to cartesian
        fun Vector3.getPositionByDistanceAndAngles(): Vector3 {

            val positionX = this.x * sin(this.z.radians()) * cos(this.y.radians())
            val positionY = this.x * sin(this.z.radians()) * sin(this.y.radians())
            val positionZ = this.x * cos(this.z.radians())

//            println("position: $this, distance: $distance, angle: $angle, new position: ($secondX, $secondY)")

            return Vector3(positionX, positionY, positionZ)
        }

        //verified with https://planetcalc.com/7952/
        //returns Pair(azimuth, polar) angles
        fun Pair<Vector3, Vector3>.anglesBetween() : Pair<Angle, Angle> {
            val azimuth = atan2((this.second.y - this.first.y), (this.second.x - this.first.x) ).degrees()

            val polarParams = sqrt((this.second.x - this.first.x).pow(2) + (this.second.y - this.first.y).pow(2)) / (this.second.z - this.first.z)
            val polar = atan(polarParams).degrees().normalizePolarDeg() //between 0 and 180 deg

            return Pair(azimuth, polar)
        }

        //transform and rotation plots verified with https://c3d.libretexts.org/CalcPlot3D/index.html
        //circle radius and angle for noise to be applied to axis firstPosition -> secondPosition at secondPosition
        //outputs position for noise

        //anglesVector == x = noiseCircleAngle, y = planarCompressAzimuth, z = planarRotateAzimuth
        //noiseCircleAngle is between 0 and 360 deg (from x-axis)
        //planarCompressAzimuth is between 0 and 90 deg (from y-axis to x-axis)
        //planarRotateAzimuth is between 0 and 90 deg (from x-axis to y-axis)
        fun Pair<Vector3, Vector3>.applyNoise(radius : Float, anglesVector : Vector3) : Vector3 {

            val angles = Pair(this.first, this.second).anglesBetween()
            val azimuth = angles.first
            val polar = angles.second

            val planeCompressedNoiseCircleAngle = anglesVector.x.planarNoiseCompress(anglesVector.y)

            val noiseCircle = Vector3(radius * cos(planeCompressedNoiseCircleAngle.radians()), radius * sin(planeCompressedNoiseCircleAngle.radians()), 0f)

            //rotate by planarRotation
            noiseCircle.rotate(Vector3(0f, 0f, 1f), anglesVector.z)
            noiseCircle.rotate(Vector3(0f, 1f, 0f), polar)
            noiseCircle.rotate(Vector3(0f, 0f, 1f), azimuth)
            noiseCircle.add(this.second)

            return noiseCircle
        }

        //compression verified with https://c3d.libretexts.org/CalcPlot3D/index.html
        //returns noise angle compressed from y-axis to x-axis by gateAzimuth
        //gateAzimuth 0 == y-axis, 90 == x-axis
        fun Angle.planarNoiseCompress(compressAzimuth : Angle) : Angle {

            val gateSize = 90f * sin(compressAzimuth.radians())
            val openAngles = 90f - gateSize
            val quadrant = (this / 90f).toInt()

            val quadAngle = when (quadrant + 1) {
                1 -> this
                2 -> 180f - this
                3 -> this - 180f
                4 -> 360 - this
                else -> 0f
            }

//            println("png() -> noise angle:$this, gate azimuth:$gateAzimuth, gate size:$gateSize, openAngles:$openAngles")
//            println("png() -> quadrant:$quadrant, quadAngle:$quadAngle")

            return when (quadrant + 1) {
                1 -> if (openAngles > 0f) quadAngle * openAngles / 90f else 0f
                2 -> if (openAngles > 0f) 180f - (quadAngle * openAngles / 90f) else 180f
                3 -> if (openAngles > 0f) 180f + (quadAngle * openAngles / 90f) else 180f
                4 -> if (openAngles > 0f) 360 - (quadAngle * openAngles / 90f) else 0f
                else -> 0f
            }
        }

    }
}