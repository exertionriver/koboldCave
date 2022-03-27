package org.river.exertion.ai.components

import org.river.exertion.ai.noumena.INoumenon.Companion.getRandomAttributes
import org.river.exertion.ai.noumena.KoboldNoumenon

class KoboldComponent : IAttributesComponent {

    override val noumenon = KoboldNoumenon()
    override var attributes = noumenon.attributables.getRandomAttributes()
}