package org.river.exertion.ai.noumena

import org.river.exertion.ai.noumena.core.INoumenon
import org.river.exertion.ai.noumena.core.InstantiatableNoumenon
import org.river.exertion.ai.noumena.core.NoumenonInstance
import org.river.exertion.ai.noumena.core.NoumenonType
import org.river.exertion.ai.noumena.other.being.humanoid.low_race.KoboldNoumenon
import java.util.*

object NoneNoumenon : INoumenon, InstantiatableNoumenon {

    override fun type() = NoumenonType.NONE
    override fun types() = listOf(type())

    fun none(lambda : NoumenonInstance.() -> Unit) = NoumenonInstance(sourceNoumenonType = NoneNoumenon.javaClass, instanceName = "none" + Random().nextInt()).apply(lambda)

}