package org.river.exertion.koboldCave.node.nodeMesh

class NodeRoomAttributes {

    enum class GeomType {NONE, LEAF, LACE, ARRAY_LATTICE, ROUNDED_LATTICE}
    enum class GeomStyle {NONE, CIRCLE, OPPOSITE, PARALLEL, SEQUENCE}

    var circleNoise : Int = 0
    var angleNoise : Int = 0
    var heightNoise : Int = 0

    var geomType : GeomType = GeomType.NONE
    var geomStyle : GeomStyle = GeomStyle.NONE
    var geomHeight : Int = 0
}