package org.river.exertion.ai.components

import org.river.exertion.ai.noumena.RedHandNoumenon

class RedHandComponent : IAttributesComponent {

    override val noumenon = RedHandNoumenon()
    override var attributes = noumenon.getRandomAttributes()
}