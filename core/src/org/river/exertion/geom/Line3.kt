package org.river.exertion.geom

import com.badlogic.gdx.math.MathUtils.cos
import com.badlogic.gdx.math.MathUtils.sin
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Vector3
import org.river.exertion.*
import org.river.exertion.geom.Line.Companion.isQ1
import org.river.exertion.geom.Line.Companion.isQ2
import org.river.exertion.geom.Line.Companion.isQ3
import org.river.exertion.geom.Line3.Companion.anglesBetween
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
/*
        fun Line3.isQ1() : Boolean {
            return ( (this.second.x - this.first.x) > 0 && (this.second.y - this.first.y) >= 0 )
        }

        fun Line3.isQ2() : Boolean {
            return ( (this.second.x - this.first.x) <= 0 && (this.second.y - this.first.y) > 0 )
        }

        fun Line3.isQ3() : Boolean {
            return ( (this.second.x - this.first.x) < 0 && (this.second.y - this.first.y) <= 0 )
        }

        fun Line3.isQ4() : Boolean {
            return ( (this.second.x - this.first.x) >= 0 && (this.second.y - this.first.y) < 0 )
        }

        fun Line3.isH1() : Boolean {
            return (this.second.z - this.first.z) >= 0
        }

        fun Line3.isH2() : Boolean {
            return (this.second.z - this.first.z) < 0
        }
*/
        //verified with https://planetcalc.com/7952/
        //returns Pair(azimuth, polar) angles
        fun Pair<Vector3, Vector3>.anglesBetween() : Pair<Angle, Angle> {
            val azimuth = atan2((this.second.y - this.first.y), (this.second.x - this.first.x) ).degrees()

            val polarParams = sqrt((this.second.x - this.first.x).pow(2) + (this.second.y - this.first.y).pow(2)) / (this.second.z - this.first.z)
            val polar = atan(polarParams).degrees().normalizePolarDeg() //between 0 and 180 deg

            return Pair(azimuth, polar)
        }

        //transform plots verified with https://c3d.libretexts.org/CalcPlot3D/index.html
        //circle radius and angle for noise to be applied to axis firstPosition -> secondPosition at secondPosition
        //outputs position for noise
        fun Pair<Vector3, Vector3>.applyNoise(radius : Float, angle : Float) : Vector3 {

            val angles = Pair(this.first, this.second).anglesBetween()
            val azimuth = angles.first
            val polar = angles.second

            val noiseCircle = Vector3(radius * cos(angle), radius * sin(angle), 0f)

            noiseCircle.rotate(Vector3(0f, 1f, 0f), polar)
            noiseCircle.rotate(Vector3(0f, 0f, 1f), azimuth).apply { this.x = -x; this.y = -y } //not sure why this needs to happen
            noiseCircle.rotate(Vector3(1f, 0f, 0f), 90f)
            noiseCircle.add(this.second)

            return noiseCircle
        }
    }
}