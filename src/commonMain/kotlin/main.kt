import com.soywiz.korev.Key
import com.soywiz.korge.Korge
import com.soywiz.korge.input.keys
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.tween.*
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Angle
import render.ButtonCommand
import render.RenderLace.renderLace
import render.RenderLattice.renderLattice
import render.RenderLeaf.renderLeaf
import render.RenderNodeLine.renderNodeLine
import render.RenderNodeMesh.renderNodeMesh
import render.RenderNodeRoom.renderNodeRoom
import render.RenderNodeRoomElaboration.renderNodeRoomElaboration
import render.RenderNodeRoomNavigation.renderNodeRoomNavigation
import render.RenderPalette

@ExperimentalUnsignedTypes
suspend fun main() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

	val commandViews = RenderPalette.initDemoScreen(this.containerRoot)

	var demoIdx = 6
	val demoSize = 8

	while (demoIdx < demoSize) {

		when (demoIdx) {
			0 -> if ( renderLeaf(this.containerRoot, commandViews) == ButtonCommand.NEXT ) demoIdx++
			1 -> if ( renderLace(this.containerRoot, commandViews) == ButtonCommand.NEXT ) demoIdx++ else demoIdx--
			2 -> if ( renderLattice(this.containerRoot, commandViews) == ButtonCommand.NEXT ) demoIdx++ else demoIdx--
			3 -> if ( renderNodeMesh(this.containerRoot, commandViews) == ButtonCommand.NEXT ) demoIdx++ else demoIdx--
			4 -> if ( renderNodeLine(this.containerRoot, commandViews) == ButtonCommand.NEXT ) demoIdx++ else demoIdx--
			5 -> if ( renderNodeRoom(this.containerRoot, commandViews) == ButtonCommand.NEXT ) demoIdx++ else demoIdx--
			6 -> if ( renderNodeRoomElaboration(this.containerRoot, commandViews) == ButtonCommand.NEXT ) demoIdx++ else demoIdx--
			7 -> if ( renderNodeRoomNavigation(this.containerRoot, commandViews) == ButtonCommand.PREV ) demoIdx--
		}
	}
}

fun Container.exploreKeys() {
	val initScale = 1.0
	var scale = initScale

	this.keys {
		down(Key.A) {
			this.view.moveBy(100, 0)
		}
		down(Key.W) {
			this.view.moveBy(0, 100)
		}
		down(Key.S) {
			this.view.moveBy(0, -100)
		}
		down(Key.D) {
			this.view.moveBy(-100, 0)
		}
		down(Key.Q) {
			this.view.rotateBy(Angle.fromDegrees(30))
		}
		down(Key.E) {
			this.view.rotateBy(Angle.fromDegrees(-30))
		}
		down(Key.Z) {
			scale += 0.25
			this.view.scaleTo(scale, scale)
		}
		down(Key.C) {
			scale -= 0.25
			this.view.scaleTo(scale, scale)
		}
		down(Key.SPACE) {
			this.view.scaleTo(initScale, initScale)
			this.view.rotateTo(Angle.fromDegrees(0))
			this.view.moveTo(0, 0)
		}
	}
}


