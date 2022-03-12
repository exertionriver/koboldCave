package org.river.exertion

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.inject.Context
import ktx.inject.register
import org.river.exertion.demos.gdx3d.Demo3d
import org.river.exertion.demos.gdx3d.Demo3dHall
import org.river.exertion.demos.gdx3d.Demo3dShapes
import org.river.exertion.demos.geom.nodeRoom.DemoNodeRoomHeightScreen
import org.river.exertion.demos.s2d.DemoNodeRoomS2DNavigateScreen

class Game : KtxGame<KtxScreen>() {
    private val context = Context()

    override fun create() {
        val bitmapFont = BitmapFont(Gdx.files.internal("fonts/OSR.fnt"), TextureRegion(Texture(Gdx.files.internal("fonts/OSR.png"), true)))
        bitmapFont.region.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        bitmapFont.data.setScale(.4F, .4F) //32 size

        val perspectiveCamera = PerspectiveCamera(75f, initViewportWidth, initViewportHeight )
        val orthoCamera = OrthographicCamera().apply { setToOrtho(false, initViewportWidth, initViewportHeight) }
//        val gameViewport = FitViewport(initViewportWidth, initViewportHeight, perspectiveCamera)
        val menuViewport = FitViewport(initViewportWidth, initViewportHeight, orthoCamera)
        val gameBatch = ModelBatch()
        val menuBatch = PolygonSpriteBatch()
        val menuStage = Stage(menuViewport, menuBatch)
        val assets = AssetManager()

        context.register {
            bindSingleton(perspectiveCamera)
            bindSingleton(orthoCamera)
            bindSingleton<Batch>(menuBatch)
            bindSingleton(gameBatch)
//            https://github.com/libgdx/libgdx/wiki/Distance-field-fonts
            bindSingleton(bitmapFont)
            bindSingleton(menuStage)
            bindSingleton(assets)

/*
    Geometries*/
/*            addScreen(DemoLeafHeightScreen( inject(), inject(), inject() ) )
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

*///            addScreen(DemoNodeRoomHeightScreen( inject(), inject(), inject() ) )

    /*navigation
            addScreen(DemoNodeRoomECSNavigateScreen( inject(), inject(), inject(), inject() ) )
          addScreen(DemoNodeRoomECSRotateNavigateScreen( inject(), inject(), inject(), inject() ) )
*/
//            addScreen(DemoNodeRoomMeshECSNavigateScreen( inject(), inject(), inject(), inject(), inject() ) )
/*            addScreen(DemoNodeRoomMeshECSRotateNavigateScreen( inject(), inject(), inject(), inject() ) )
*/
//            addScreen(DemoNodeRoomS2DNavigateScreen( inject(), inject(), inject(), inject(), inject() ) )
//            addScreen(DemoS2DTableScrollUI( inject(), inject(), inject(), inject(), inject() ) )

//            addScreen(DemoFSM( inject(), inject(), inject(), inject(), inject() ) )
//            addScreen(Demo3d( inject(), inject(), inject(), inject(), inject(), inject(), inject() ) )
//            addScreen(Demo3dShapes( inject(), inject(), inject(), inject(), inject(), inject(), inject() ) )
            addScreen(Demo3dHall( inject(), inject(), inject(), inject(), inject(), inject(), inject() ) )

        }

        setScreen<Demo3dHall>()
//        super.create()
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
