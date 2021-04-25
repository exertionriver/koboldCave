import com.soywiz.korge.Korge
import com.soywiz.korge.resources.resourceBitmap
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.scale
import com.soywiz.korio.resources.ResourcesContainer
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import render.ButtonCommand
import render.RenderLaceLash.renderLaceCircle
import render.RenderLaceLash.renderLaceLashAngled
import render.RenderLaceLash.renderLaceLashStationary
import render.RenderLattice.renderLatticeStationary
import render.RenderLeaf
import render.RenderLeaf.renderBorderingLeaf
import render.RenderLeaf.renderLeaf
import render.RenderLeaf.renderLeafCircle
import render.RenderLeaf.renderLeafStationary
import render.RenderLeaf.renderPruneLeaf
import render.RenderNodeMesh.renderNodeMeshRooms
import render.RenderNodeMesh.renderNodeMeshRoomsSetCentroids
import render.RenderNodeMesh.renderNodeMeshStationaryOperations
import render.RenderNodeMesh.renderNodeMeshStationaryOperationsExtended
import render.RenderNodeMesh.renderOrphanHandlingNodeMeshRooms
import render.RenderNodeMesh.renderOrphanedNodeMesh
import render.RenderNodeRooms.renderConnectedNodeRoomBorder
import render.RenderNodeRooms.renderConnectedNodeRoomElaboration
import render.RenderNodeRooms.renderConnectedNodeRooms
import render.RenderNodeRooms.renderNodeRoomsBuiltLines
import render.RenderPalette

@ExperimentalUnsignedTypes
suspend fun main() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

	val commandViews = RenderPalette.initDemoScreen(this.containerRoot)

	renderLeaf(this.containerRoot, commandViews)

//	renderLaceLashStationary()

// renderLatticeStationary()

//	renderLaceLashAngled()

//	renderLaceCircle()

//	renderLeafAndNodes()

//	renderNodeMeshStationaryOperations()

//	renderNodeMeshStationaryOperationsExtended()

//	renderAbsorbedNodeMesh()

//	renderNodeMeshRooms()

//	renderNodeMeshRoomsSetCentroids()

//	renderOrphanedNodeMesh()

//	renderOrphanHandlingNodeMeshRooms()

//	renderNodeLineStationary()

//	renderNodeLinesBetweenPoints()

//	renderNodeRooms()

	//proof of concept only -- not used, takes too long to run
//	renderNodeRoomsBuiltLines()

//	renderConnectedNodeRooms()

//	renderConnectedNodeRoomBorder()

//	renderConnectedNodeRoomElaboration()

//	renderArrow()

//	renderNavigation()

//	renderElaboratingNavigation()
}


