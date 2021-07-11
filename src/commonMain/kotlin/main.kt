import com.soywiz.korge.Korge
import com.soywiz.korim.color.Colors
import render.ButtonCommand
import render.RenderLace.renderLace
import render.RenderLattice.renderLattice
import render.RenderLeaf.renderLeaf
import render.RenderNodeLine.renderNodeLine
import render.RenderNodeMesh.renderNodeMesh
import render.RenderNodeRooms.renderNodeRooms
import render.RenderPalette

@ExperimentalUnsignedTypes
suspend fun main() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

	val commandViews = RenderPalette.initDemoScreen(this.containerRoot)

	var demoIdx = 4
	val demoSize = 6

	while (demoIdx < demoSize) {

		when (demoIdx) {
			0 -> if ( renderLeaf(this.containerRoot, commandViews) == ButtonCommand.NEXT ) demoIdx++
			1 -> if ( renderLace(this.containerRoot, commandViews) == ButtonCommand.NEXT ) demoIdx++ else demoIdx--
			2 -> if ( renderLattice(this.containerRoot, commandViews) == ButtonCommand.NEXT ) demoIdx++ else demoIdx--
			3 -> if ( renderNodeMesh(this.containerRoot, commandViews) == ButtonCommand.NEXT ) demoIdx++ else demoIdx--
			4 -> if ( renderNodeLine(this.containerRoot, commandViews) == ButtonCommand.NEXT ) demoIdx++ else demoIdx--
			5 -> if ( renderNodeRooms(this.containerRoot, commandViews) == ButtonCommand.PREV ) demoIdx--
//			5 -> if ( renderNodeRooms(this.containerRoot, commandViews) == ButtonCommand.NEXT ) demoIdx++ else demoIdx--
//  future directions:
//			6 -> if ( renderNavigation(this.containerRoot, commandViews) == ButtonCommand.PREV ) demoIdx--
		}
	}
}


