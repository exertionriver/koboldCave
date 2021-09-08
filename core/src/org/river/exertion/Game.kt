package org.river.exertion

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.inject.Context
import ktx.inject.register
import org.river.exertion.screen.nodeRoom.DemoNodeRoomSlopeScreen
import org.river.exertion.screen.nodeRoom.DemoNodeRoomWallFloorScreen
import org.river.exertion.screen.nodeRoomMesh.DemoNodeRoomMeshNavigateScreen

class Game : KtxGame<KtxScreen>() {
    private val context = Context()

    override fun create() {
        val bitmapFont = BitmapFont(Gdx.files.internal("fonts/OSR.fnt"), TextureRegion(Texture(Gdx.files.internal("fonts/OSR.png"), true)))
        bitmapFont.region.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        bitmapFont.data.setScale(.4F, .4F) //32 size
        val camera = OrthographicCamera().apply { setToOrtho(false, initViewportWidth, initViewportHeight) }
        val viewport = FitViewport(initViewportWidth, initViewportHeight, camera)
        val batch = PolygonSpriteBatch()
        val stage = Stage(viewport, batch)
        val assets = AssetManager()

        context.register {
            bindSingleton<Batch>(batch)
//            https://github.com/libgdx/libgdx/wiki/Distance-field-fonts
            bindSingleton(bitmapFont)
            bindSingleton(camera)
            bindSingleton(stage)
            bindSingleton(assets)
                /*
            addScreen(DemoLeafHeightScreen( inject(), inject(), inject() ) )
            addScreen(DemoLeafAngledScreen( inject(), inject(), inject() ) )
            addScreen(DemoLeafBorderingScreen( inject(), inject(), inject() ) )

            addScreen(DemoLaceHeightScreen( inject(), inject(), inject() ) )
            addScreen(DemoLaceAngledScreen( inject(), inject(), inject() ) )
            addScreen(DemoLaceBorderingScreen( inject(), inject(), inject() ) )

            addScreen(DemoArrayLatticeHeightScreen( inject(), inject(), inject() ) )
            addScreen(DemoArrayLatticeAngledScreen( inject(), inject(), inject() ) )
            addScreen(DemoArrayLatticeBorderingScreen( inject(), inject(), inject() ) )

            addScreen(DemoRoundedLatticeHeightScreen( inject(), inject(), inject() ) )
            addScreen(DemoRoundedLatticeAngledScreen( inject(), inject(), inject() ) )
            addScreen(DemoRoundedLatticeBorderingScreen( inject(), inject(), inject() ) )

            addScreen(DemoNodeLineHeightScreen( inject(), inject(), inject() ) )
            addScreen(DemoNodeLineAngledScreen( inject(), inject(), inject() ) )
            addScreen(DemoNodeLineBorderingScreen( inject(), inject(), inject() ) )

            addScreen(DemoNodeMeshOperationsFirstScreen( inject(), inject(), inject() ) )
            addScreen(DemoNodeMeshOperationsSecondScreen( inject(), inject(), inject(), inject()) )
            addScreen(DemoNodeMeshOperationsThirdScreen( inject(), inject(), inject(), inject()) )

            addScreen(DemoNodeRoomHeightScreen( inject(), inject(), inject() ) )
       *///      addScreen(DemoNodeRoomWallFloorScreen( inject(), inject(), inject() ) )
       //     addScreen(DemoNodeRoomSlopeScreen( inject(), inject(), inject() ) )
       //     addScreen(DemoNodeRoomNavigateScreen( inject(), inject(), inject(), inject() ) )

            addScreen(DemoNodeRoomMeshNavigateScreen( inject(), inject(), inject(), inject() ) )
        }

        setScreen<DemoNodeRoomMeshNavigateScreen>()
        super.create()
    }

    override fun dispose() {
        context.dispose()
        super.dispose()
    }

    companion object {
        val initViewportWidth = 1024F
        val initViewportHeight = 576F
    }
}
