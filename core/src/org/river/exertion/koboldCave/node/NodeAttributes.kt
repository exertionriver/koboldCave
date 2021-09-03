package org.river.exertion.koboldCave.node

import org.river.exertion.Point
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.NextDistancePx

class NodeAttributes {

    enum class NodeType {
        CENTROID //usually not within rendered mesh nodes
        , START
        , EXIT
        , OPEN }
    var nodeType : NodeType = NodeType.OPEN

    override fun toString(): String {
        return "${super.toString()}, $nodeType"
    }

}