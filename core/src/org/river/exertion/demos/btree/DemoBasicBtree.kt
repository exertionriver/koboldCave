package org.river.exertion.demos.btree

import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction
import ktx.app.KtxScreen
import org.river.exertion.*
import org.river.exertion.assets.*
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.IBTCharacter
import org.river.exertion.geom.node.nodeMesh.NodeRoom
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh
import org.river.exertion.btree.v0_1.KoboldCharacter
import org.river.exertion.btree.v0_1.TaskEnum

class DemoBasicBtree(private val batch: Batch,
                     private val font: BitmapFont,
                     private val assets: AssetManager,
                     private val stage: Stage,
                     private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 11
    val vertOffset = Game.initViewportHeight / 11
    val labelVert = Point(0F, Game.initViewportHeight * 2 / 32)

    var nodeRoom = NodeRoom(height = 3, centerPoint = Point(horizOffset * 5.5f, vertOffset * 5.5f))
    var nodeRoomMesh = NodeRoomMesh(nodeRoom)

    val koboldCharacter1 = KoboldCharacter()
    val koboldCharacter2 = KoboldCharacter()

    val controlAreaCamera = OrthographicCamera()

    
    override fun render(delta: Float) {

    koboldCharacter1.update(delta)
//    koboldCharacter2.update(delta)

    }

    override fun hide() {
    }

    override fun show() {
        Gdx.app.logLevel = LOG_DEBUG

        // start the playback of the background music when the screen is shown
        MusicAssets.values().forEach { assets.load(it) }
        assets.finishLoading()
//        println("done!")
        assets[MusicAssets.NavajoNight].apply { isLooping = true }.play()
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
    }

    override fun dispose() {
        assets.dispose()
    }
}