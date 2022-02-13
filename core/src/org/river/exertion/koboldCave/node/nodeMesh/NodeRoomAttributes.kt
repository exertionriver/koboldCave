package org.river.exertion.koboldCave.node.nodeMesh

class NodeRoomAttributes {

    enum class GeomType {LEAF, LACE, ARRAY_LATTICE, ROUNDED_LATTICE}
    enum class GeomStyle {CIRCLE, OPPOSITE, SINGLE, SEQUENCE}

    var circleNoise : Int = 0
    var angleNoise : Int = 0
    var heightNoise : Int = 0

    var pathThickness = 0.2f
    var wallThickness = 0.1f
    var wallFadeThickness = 0.2f
    var centerToEdgeThicknessVariance = 0.1f

    var geomType : GeomType = GeomType.LEAF
    var geomStyle : GeomStyle = GeomStyle.CIRCLE
    var geomHeight : Int = 3
}