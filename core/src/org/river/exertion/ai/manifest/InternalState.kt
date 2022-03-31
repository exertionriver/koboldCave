package org.river.exertion.ai.manifest

import com.badlogic.gdx.math.Vector2

data class InternalState(val xGrid : Vector2?, val yGrid : Vector2?, val zGrid : Vector2?, val aGrid : Vector2 = Vector2(1f, 0f) ) {

//xGrid in encounters : x is self-assessed strength, y is other-assessed strength
//yGrid in encounters : x is self-assessed weakness, y is other-assessed weakness
//zGrid in encounters : x is threat, y is opportunity
//aGrid in encounters : x is magnitude, y is degree of clarity(+) / confusion(-)

    companion object {
        fun InternalState.magnitude() = this.aGrid.x

        operator fun InternalState.plus(secondState : InternalState) = InternalState(
                when {
                    (this.xGrid == null || secondState.xGrid == null) -> null
                    else -> Vector2(this.xGrid.x + secondState.xGrid.x, this.xGrid.y + secondState.xGrid.y)
                },
                when {
                    (this.yGrid == null || secondState.yGrid == null) -> null
                    else -> Vector2(this.yGrid.x + secondState.yGrid.x, this.yGrid.y + secondState.yGrid.y)
                },
                when {
                    (this.zGrid == null || secondState.zGrid == null) -> null
                    else -> Vector2(this.zGrid.x + secondState.zGrid.x, this.zGrid.y + secondState.zGrid.y)
                },
                Vector2(this.aGrid.x, this.aGrid.y)
        )

        operator fun InternalState.minus(secondState : InternalState) = InternalState(
            when {
                (this.xGrid == null || secondState.xGrid == null) -> null
                else -> Vector2(this.xGrid.x - secondState.xGrid.x, this.xGrid.y - secondState.xGrid.y)
            },
            when {
                (this.yGrid == null || secondState.yGrid == null) -> null
                else -> Vector2(this.yGrid.x - secondState.yGrid.x, this.yGrid.y - secondState.yGrid.y)
            },
            when {
                (this.zGrid == null || secondState.zGrid == null) -> null
                else -> Vector2(this.zGrid.x - secondState.zGrid.x, this.zGrid.y - secondState.zGrid.y)
            },
            Vector2(this.aGrid.x, this.aGrid.y)
        )

        operator fun InternalState.div(scalar : Int) = InternalState(
                when {
                    (this.xGrid == null) -> null
                    else -> Vector2(this.xGrid.x / scalar, this.xGrid.y / scalar)
                },
                when {
                    (this.yGrid == null) -> null
                    else -> Vector2(this.yGrid.x / scalar, this.yGrid.y / scalar)
                },
                when {
                    (this.zGrid == null) -> null
                    else -> Vector2(this.zGrid.x / scalar, this.zGrid.y / scalar)
                },
                Vector2(this.aGrid.x, this.aGrid.y)
        )

        fun InternalState.scaleToMagnitude() = InternalState(
                when {
                    (this.xGrid == null) -> null
                    else -> Vector2(this.xGrid.x * this.magnitude(), this.xGrid.y * this.magnitude())
                },
                when {
                    (this.yGrid == null) -> null
                    else -> Vector2(this.yGrid.x * this.magnitude(), this.yGrid.y * this.magnitude())
                },
                when {
                    (this.zGrid == null) -> null
                    else -> Vector2(this.zGrid.x * this.magnitude(), this.zGrid.y * this.magnitude())
                },
                Vector2(1f, this.aGrid.y)
        )
    }

}