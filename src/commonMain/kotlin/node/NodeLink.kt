package node

import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import leaf.ILeaf
import leaf.Leaf
import kotlin.random.Random

@ExperimentalUnsignedTypes
class NodeLink(val firstNodeUuid : UUID, val secondNodeUuid : UUID, val distance : Double, val firstToSecondAngle : Angle, val secondToFirstAngle : Angle) {

    constructor(firstLeaf : ILeaf, secondLeaf : ILeaf) : this (
        firstNodeUuid = firstLeaf.uuid
        , secondNodeUuid = secondLeaf.uuid
        , distance = firstLeaf.position.distanceTo(secondLeaf.position)
        , firstToSecondAngle = Angle.between(firstLeaf.position, secondLeaf.position)
        , secondToFirstAngle = Angle.between(secondLeaf.position, firstLeaf.position)
    )

    constructor(copyNodeLink : NodeLink
                , updFirstNodeUuid: UUID = copyNodeLink.firstNodeUuid
                , updSecondNodeUuid: UUID = copyNodeLink.secondNodeUuid
                , updDistance: Double = copyNodeLink.distance
                , updFirstToSecondAngle: Angle = copyNodeLink.firstToSecondAngle
                , updSecondToFirstAngle: Angle = copyNodeLink.secondToFirstAngle) : this (
        firstNodeUuid = updFirstNodeUuid
        , secondNodeUuid = updSecondNodeUuid
        , distance = updDistance
        , firstToSecondAngle = updFirstToSecondAngle
        , secondToFirstAngle = updSecondToFirstAngle
    )

    override fun toString() = "${NodeLink::class.simpleName}($firstNodeUuid, $secondNodeUuid) : $distance, $firstToSecondAngle, $secondToFirstAngle"
}