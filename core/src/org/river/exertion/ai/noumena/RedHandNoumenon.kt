package org.river.exertion.ai.noumena

import org.river.exertion.ai.attributes.AttributeRange.Companion.mergeOverrideAttributeRanges

class RedHandNoumenon : INoumenon {

    override fun tag() = "red hand"
    override fun tags() = GroupNoumenon.tags().toMutableList().apply { this.add( tag() ) }.toList()
    override fun attributeRange() = GroupNoumenon.attributeRange().mergeOverrideAttributeRanges(listOf())
}