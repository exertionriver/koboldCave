package org.river.exertion.ai.manifest

import com.badlogic.gdx.math.Vector2

object InternalStateBiases {

    //passion
    fun desire() = InternalState(Vector2(0f, 0f), Vector2(-1f,1f), Vector2(0f, 1f), Vector2(1f, -1f))
    fun disgust() = InternalState(Vector2(0f, 0f), Vector2(1f, -1f), Vector2(0f, -1f), Vector2(1f, -1f))
    fun envyGreed() = InternalState(Vector2(0f, -1f), Vector2(0f, 1f), Vector2(0f, 1f), Vector2(1f, -1f))
    fun adoration() = InternalState(Vector2(0f, 0f), Vector2(1f, 1f), Vector2(1f, 1f), Vector2(1f, -1f))
    fun arrogance() = InternalState(Vector2(1f, 0f), Vector2(-1f, 0f), Vector2(-1f, 0f), Vector2(1f, -1f))

    //aggression
    fun anger() = InternalState(Vector2(1f, -1f), Vector2(0f, 0f), Vector2(-1f, 0f), Vector2(1f, -1f))
    fun fear() = InternalState(Vector2(-1f, 1f), Vector2(0f, 0f), Vector2(1f, 0f), Vector2(1f, -1f))
    fun jealousy() = InternalState(Vector2(0f, 1f), Vector2(0f, -1f), Vector2(1f, 0f), Vector2(1f, -1f))
    fun rage() = InternalState(Vector2(1f, 1f), Vector2(0f, 0f), Vector2(-1f, 1f), Vector2(1f, -1f))
    fun disdain() = InternalState(Vector2(0f, 0f), Vector2(-1f, -1f), Vector2(1f, -1f), Vector2(1f, -1f))

    //ignorance
    fun delusion() = InternalState(null, null, null, Vector2(1f, -1f))
    fun doubt() = InternalState(Vector2(-1f, 0f), Vector2(1f, 0f), null, Vector2(1f, -1f))
    fun pride() = InternalState(null, null, Vector2(0f, -1f), Vector2(1f, -1f))
    fun indolence() = InternalState(Vector2(-1f, -1f), Vector2(0f, 0f), Vector2(-1f, -1f), Vector2(1f, -1f))

    fun none() = InternalState(Vector2(0f, 0f), Vector2(0f, 0f), Vector2(0f, 0f), Vector2(0f, 0f))
}