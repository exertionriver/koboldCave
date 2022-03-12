package org.river.exertion.demos.gdx3d

import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute.createDiffuse
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.environment.SpotLight
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.graphics.g3d.utils.AnimationController
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.math.MathUtils.sin
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.UBJsonReader
import com.badlogic.gdx.utils.viewport.StretchViewport
import ktx.app.KtxScreen
import ktx.math.plus
import ktx.scene2d.*
import org.river.exertion.*
import org.river.exertion.assets.TextureAssets
import org.river.exertion.assets.get
import org.river.exertion.assets.load
import org.river.exertion.gdx3d.BasicShader
import org.river.exertion.geom.node.Node
import org.river.exertion.geom.node.Node3
import org.river.exertion.geom.node.nodeMesh.NodeLine3
import org.river.exertion.geom.node.nodeMesh.NodeRoom
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh

class Demo3dHallElevationObstacle(private val menuBatch: Batch,
                                  private val gameBatch: ModelBatch,
                                  private val font: BitmapFont,
                                  private val assets: AssetManager,
                                  private val menuStage: Stage,
                                  private val menuCamera: OrthographicCamera,
                                  private val gameCamera: PerspectiveCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 11
    val vertOffset = Game.initViewportHeight / 11
    val labelVert = Point(0F, Game.initViewportHeight * 2 / 32)

    var nodeRoom = NodeRoom(height = 3, centerPoint = Point(horizOffset * 5.5f, vertOffset * 5.5f))
    var nodeRoomMesh = NodeRoomMesh(nodeRoom)

//    val engine = PooledEngine().apply { SystemManager.init(this) }
//    val cave = LocationCave.instantiate(engine, menuStage, "spookyCave", nodeRoomMesh)
//    val playerCharacter = CharacterPlayerCharacter.instantiate(engine, menuStage, location = cave, camera = null)

//    val controlAreaCamera = OrthographicCamera()
    val gameAreaViewport = StretchViewport(Game.initViewportWidth, Game.initViewportHeight, gameCamera)

    val sdc = ShapeDrawerConfig(menuBatch)
    val drawer = sdc.getDrawer()

    val modelBuilder = ModelBuilder()

    lateinit var modelLightInstance : ModelInstance
    lateinit var modelWallsInstance : ModelInstance
    lateinit var modelFloorsInstance : ModelInstance
    lateinit var modelEntityInstance : ModelInstance

    val modelEnvironment = Environment()
    val floorEnvironment = Environment()
    val wallEnvironment = Environment()

    val overhead = Vector3(15f, 60f, 150f)
    val frontHall = Vector3(15f, 150f, 17.5f)
    val backHall = Vector3(15f, 10f, 17.5f)
    val centerHall = Vector3(15f, 60f, 17.5f)

    val lookAt = Vector3(15f, 60f, 1f)

    var lightX = overhead.x
    var lightY = overhead.y
    var lightZ = overhead.z

    lateinit var currentModelPosition : Vector3
    var currentAnimationPosition = 0f
    var currentAnimationDirection = 270f

    var walkingAnimationDuration = 0f
    var walkingAnimationDistance = 0f

    var stoppingAnimationDuration = 0f
    var stoppingAnimationDistance = 0f

    val dirLight = DirectionalLight().setDirection(overhead + Vector3(0f, 0f, 300f)).setColor(Color.PURPLE)
    val spotLight = SpotLight().setPosition(overhead).setDirection(lookAt).setIntensity(0.8f)

    val shader = BasicShader().apply { this.init() }

    lateinit var modelLight : Model
    lateinit var modelWalls : Model
    lateinit var modelFloors : Model
    lateinit var modelEntity : Model

    val renderContext = RenderContext(DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN, 1));

    lateinit var animationController : AnimationController


    fun updateModel(currentAnimationId : String, nextAnimationId : String) {
        if (animationController.current != null) {

            if ((currentAnimationId == "LeftTurn") || (currentAnimationId == "RightTurn")) {
                modelEntityInstance.transform.rotate(Vector3(0f, 1f, 0f), 180f)
                currentAnimationDirection += 180
                currentAnimationDirection = currentAnimationDirection.normalizeDeg()
            }
            if (currentAnimationId == "StopWalking") {
                modelEntityInstance.transform.trn(Vector3(0f, stoppingAnimationDistance * sin(currentAnimationDirection.radians()), 0f))
                currentModelPosition += Vector3(0f, stoppingAnimationDistance * sin(currentAnimationDirection.radians()), 0f)
            }
            if (currentAnimationId == "Walking") {
                currentAnimationPosition = walkingAnimationDistance * (animationController.current.time / animationController.current.duration)

                currentModelPosition += if (nextAnimationId == currentAnimationId) { // walking loop
                    modelEntityInstance.transform.trn(Vector3(0f, walkingAnimationDistance * sin(currentAnimationDirection.radians()), 0f))
                    Vector3(0f, walkingAnimationDistance * sin(currentAnimationDirection.radians()), 0f)
                } else { //interrupt walking
                    modelEntityInstance.transform.trn(Vector3(0f, currentAnimationPosition * sin(currentAnimationDirection.radians()), 0f))
                    Vector3(0f, currentAnimationPosition * sin(currentAnimationDirection.radians()), 0f)
                }
            }
        }
    }

    @Suppress("NewApi")
    override fun render(delta: Float) {

        //https://stackoverflow.com/questions/35969253/libgdx-antialiasing
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0))
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        InputHandler.handleInput(gameCamera, lookAt)

        when {
            Gdx.input.isKeyJustPressed(Input.Keys.UP) -> {
                if (animationController.current != null) updateModel(animationController.current.animation.id, "Walking")
                animationController.setAnimation("Walking",-1, 0.8f, object : AnimationListener {
                    override fun onEnd(animation: AnimationController.AnimationDesc?) {
                        updateModel(animationController.current.animation.id, "StopWalking")
                    }
                    override fun onLoop(animation: AnimationController.AnimationDesc?) {
                        updateModel(animationController.current.animation.id, animationController.current.animation.id)
                    }
                } )
            }
            Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> {
                if ( (animationController.current != null) && (animationController.current.animation.id == "Walking") ) {
                    animationController.current.loopCount = 1
                        animationController.queue("StopWalking", 1, 0.8f, object : AnimationListener {
                            override fun onEnd(animation: AnimationController.AnimationDesc?) {}
                            override fun onLoop(animation: AnimationController.AnimationDesc?) {}
                        }, 100f)
                }
            }
            Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> {
                if (animationController.current != null) {
                    updateModel(animationController.current.animation.id, "LeftTurn")
                    if (animationController.current.animation.id == "LeftTurn") {
                        animationController.animate("LeftTurn", 0f)
                    } else {
                        animationController.setAnimation("LeftTurn", 1)
                    }
                } else {
                    animationController.setAnimation("LeftTurn", 1)
                }
            }
            Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> {
                if (animationController.current != null) {
                    updateModel(animationController.current.animation.id, "RightTurn")
                    if (animationController.current.animation.id == "RightTurn") {
                        animationController.animate("RightTurn", 0f)
                    } else {
                        animationController.setAnimation("RightTurn",1)
                    }
                } else {
                    animationController.setAnimation("RightTurn",1)
                }
            }

            Gdx.input.isKeyJustPressed(Input.Keys.T) -> { spotLight.setDirection(overhead); modelLightInstance = ModelInstance(modelLight, overhead) }
            Gdx.input.isKeyJustPressed(Input.Keys.Y) -> { spotLight.setDirection(frontHall); modelLightInstance = ModelInstance(modelLight, frontHall) }
            Gdx.input.isKeyJustPressed(Input.Keys.U) -> { spotLight.setDirection(backHall); modelLightInstance = ModelInstance(modelLight, backHall) }
            Gdx.input.isKeyJustPressed(Input.Keys.I) -> { spotLight.setDirection(centerHall); modelLightInstance = ModelInstance(modelLight, centerHall) }
            Gdx.input.isKeyJustPressed(Input.Keys.G) -> { gameCamera.position.set(overhead); gameCamera.lookAt(lookAt) }
            Gdx.input.isKeyJustPressed(Input.Keys.H) -> { gameCamera.position.set(frontHall); gameCamera.lookAt(backHall) }
            Gdx.input.isKeyJustPressed(Input.Keys.J) -> { gameCamera.position.set(backHall); gameCamera.lookAt(frontHall) }
            Gdx.input.isKeyJustPressed(Input.Keys.K) -> { gameCamera.position.set(centerHall); gameCamera.lookAt(Vector3(frontHall.x, frontHall.y, -1f)) }

            Gdx.input.isKeyJustPressed(Input.Keys.B) -> { lightX += 10f ; spotLight.setDirection(lightX, lightY, lightZ); modelLightInstance = ModelInstance(modelLight, Vector3(lightX, lightY, lightZ)) }
            Gdx.input.isKeyJustPressed(Input.Keys.N) -> { lightY += 10f ; spotLight.setDirection(lightX, lightY, lightZ); modelLightInstance = ModelInstance(modelLight, Vector3(lightX, lightY, lightZ)) }
            Gdx.input.isKeyJustPressed(Input.Keys.M) -> { lightZ += 10f ; spotLight.setDirection(lightX, lightY, lightZ); modelLightInstance = ModelInstance(modelLight, Vector3(lightX, lightY, lightZ)) }
            Gdx.input.isKeyJustPressed(Input.Keys.COMMA) -> { lightX -= 10f ; spotLight.setDirection(lightX, lightY, lightZ); modelLightInstance = ModelInstance(modelLight, Vector3(lightX, lightY, lightZ)) }
            Gdx.input.isKeyJustPressed(Input.Keys.PERIOD) -> { lightY -= 10f ; spotLight.setDirection(lightX, lightY, lightZ); modelLightInstance = ModelInstance(modelLight, Vector3(lightX, lightY, lightZ))}
            Gdx.input.isKeyJustPressed(Input.Keys.SLASH) -> { lightZ -= 10f ; spotLight.setDirection(lightX, lightY, lightZ); modelLightInstance = ModelInstance(modelLight, Vector3(lightX, lightY, lightZ)) }

        }

        gameCamera.update()
        animationController.update(delta)

        Gdx.app.log("animation position / direction", "$currentAnimationPosition / $currentAnimationDirection")
        Gdx.app.log("model position", "$currentModelPosition")

//        renderContext.begin()
        gameBatch.begin(gameCamera)
//        shader.begin(gameCamera, renderContext)

//        gameBatch.render(modelWallsInstance, shader)
//        gameBatch.render(modelLightInstance, environment)
        gameBatch.render(modelWallsInstance, wallEnvironment)
        gameBatch.render(modelFloorsInstance, floorEnvironment)
        gameBatch.render(modelEntityInstance, modelEnvironment)
        gameBatch.end()


//        Gdx.app.log("camera", "position: ${gameCamera.position}, direction: ${gameCamera.direction}")
    }

    override fun hide() {
    }

    override fun show() {
        Render.initRender(gameCamera, overhead, lookAt)

        val point = Point(lookAt.x, lookAt.y)
        Render.initRender(menuCamera, Node(position = point), Render.cameraAngle)

        Scene2DSkin.defaultSkin = Skin(Gdx.files.internal("skin/clean-crispy-ui.json"))
        TextureAssets.values().forEach { assets.load(it) }
        assets.finishLoading()

        modelLight = modelBuilder.createSphere(5f, 5f, 5f, 20, 20, Material(createDiffuse(Color.BLUE)), (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        modelLightInstance = ModelInstance(modelLight, overhead)

        modelFloorsInstance = ModelInstance(modelBuilder.createLineGrid(10, 10, 10f, 10f, Material(createDiffuse(Color.BLUE)), (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()), lookAt.x, lookAt.y, 0f)

        var prevNodeLine3 = NodeLine3()
        var currNodeLine3 = NodeLine3()
        val floorNoise = Vector3(100f, 70f, 90f)
        val wallNoise = Vector3(100f, 0f, 90f)
        val attr = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong()

        modelBuilder.begin()
        //floor
        (5 .. 25 ).forEach { xIdx ->

            val xIdxNoise = Probability(0, 10).getValue() / 100f
            val zIdxNoise = 1f + Probability(0, 10).getValue() / 100f

            if (xIdx == 5)
                currNodeLine3 = NodeLine3(firstNode = Node3(position = Vector3(xIdx + xIdxNoise, 0f, 0f + zIdxNoise)), lastNode = Node3(position = Vector3(xIdx + xIdxNoise, 120f, 0f + zIdxNoise)), lineNoise = floorNoise)
            else {
                prevNodeLine3 = currNodeLine3
                currNodeLine3 = NodeLine3(firstNode = Node3(position = Vector3(xIdx + xIdxNoise, 0f, 0f + zIdxNoise)), lastNode = Node3(position = Vector3(xIdx + xIdxNoise, 120f, 0f + zIdxNoise)), lineNoise = floorNoise)

                val pnl3Vertices = prevNodeLine3.getPositions()
                val cnl3Vertices = currNodeLine3.getPositions()

                //assuming they are same size?
                (1 until pnl3Vertices.size).forEach { idx ->
                    val normal = Vector3(pnl3Vertices[idx - 1])
                    modelBuilder.part("id${idx}", GL20.GL_TRIANGLES, attr, Material(TextureAttribute.createDiffuse(assets[TextureAssets.CaveWall]))).rect(
                            pnl3Vertices[idx - 1], cnl3Vertices[idx - 1], cnl3Vertices[idx], pnl3Vertices[idx], normal.crs(cnl3Vertices[idx])
                    )
                }
            }
        }
        modelFloors = modelBuilder.end()
        modelFloorsInstance = ModelInstance(modelFloors, 0f, 10f, 0f)

        modelBuilder.begin()
        //side wall 1
        (10 ..110 step 2).forEach { yIdx ->

            val xIdxNoise1 = Probability(0, 5).getValue() / 10f
            val yIdxNoise = Probability(yIdx, 2).getValue()

            if (yIdx == 10)
                currNodeLine3 = NodeLine3(firstNode = Node3(position = Vector3(5f + xIdxNoise1, yIdxNoise, 0f)), lastNode = Node3(position = Vector3(5f + xIdxNoise1, yIdxNoise, 30f)), lineNoise = wallNoise)
            else {
                prevNodeLine3 = currNodeLine3
                currNodeLine3 = NodeLine3(firstNode = Node3(position = Vector3(5f + xIdxNoise1, yIdxNoise, 0f)), lastNode = Node3(position = Vector3(5f + xIdxNoise1, yIdxNoise, 30f)), lineNoise = wallNoise)

                val pnl3Vertices = prevNodeLine3.getPositions()
                val cnl3Vertices = currNodeLine3.getPositions()

                if (pnl3Vertices.size != cnl3Vertices.size) Gdx.app.log("vertices:", "${pnl3Vertices.size} != ${cnl3Vertices.size}")

                //assuming they are same size?
                (1 until pnl3Vertices.size).forEach { idx ->
                    val normal = Vector3(pnl3Vertices[idx - 1])
                    modelBuilder.part("id${idx}", GL20.GL_TRIANGLES, attr, Material(TextureAttribute.createDiffuse(assets[TextureAssets.CaveWall]))).rect(
                            pnl3Vertices[idx - 1], cnl3Vertices[idx - 1], cnl3Vertices[idx], pnl3Vertices[idx], normal.crs(cnl3Vertices[idx])
                    )
                }
            }
        }
        //top wall 1
        (0 .. 7).forEach { xIdx ->

            val xIdxNoise = Probability(0, 10).getValue() / 100f
            val zIdxNoise = 1f + Probability(0, 10).getValue() / 100f

            if (xIdx == 0)
                currNodeLine3 = NodeLine3(firstNode = Node3(position = Vector3(xIdx + xIdxNoise, 10f, 30.5f + zIdxNoise)), lastNode = Node3(position = Vector3(xIdx + xIdxNoise, 110f, 30.5f + zIdxNoise)), lineNoise = wallNoise)
            else {
                prevNodeLine3 = currNodeLine3
                currNodeLine3 = NodeLine3(firstNode = Node3(position = Vector3(xIdx + xIdxNoise, 10f, 30.5f + zIdxNoise)), lastNode = Node3(position = Vector3(xIdx + xIdxNoise, 110f, 30.5f + zIdxNoise)), lineNoise = wallNoise)

                val pnl3Vertices = prevNodeLine3.getPositions()
                val cnl3Vertices = currNodeLine3.getPositions()

                if (pnl3Vertices.size != cnl3Vertices.size) Gdx.app.log("vertices:", "${pnl3Vertices.size} != ${cnl3Vertices.size}")

                //assuming they are same size?
                (1 until pnl3Vertices.size).forEach { idx ->
                    val normal = Vector3(pnl3Vertices[idx - 1])
                    modelBuilder.part("id${idx}", GL20.GL_TRIANGLES, attr, Material(TextureAttribute.createDiffuse(assets[TextureAssets.CaveWall]))).rect(
                            pnl3Vertices[idx - 1], cnl3Vertices[idx - 1], cnl3Vertices[idx], pnl3Vertices[idx], normal.crs(cnl3Vertices[idx])
                    )
                }
            }
        }
        //side wall 2
        (110 downTo 10 step 2).forEach { yIdx ->

            val xIdxNoise1 = Probability(0, 5).getValue() / 10f
            val yIdxNoise = Probability(yIdx, 2).getValue()

            if (yIdx == 110)
                currNodeLine3 = NodeLine3(firstNode = Node3(position = Vector3(25f + xIdxNoise1, yIdxNoise, 0f)), lastNode = Node3(position = Vector3(25f + xIdxNoise1, yIdxNoise, 30f)), lineNoise = wallNoise)
            else {
                prevNodeLine3 = currNodeLine3
                currNodeLine3 = NodeLine3(firstNode = Node3(position = Vector3(25f + xIdxNoise1, yIdxNoise, 0f)), lastNode = Node3(position = Vector3(25f + xIdxNoise1, yIdxNoise, 30f)), lineNoise = wallNoise)

                val pnl3Vertices = prevNodeLine3.getPositions()
                val cnl3Vertices = currNodeLine3.getPositions()

                if (pnl3Vertices.size != cnl3Vertices.size) Gdx.app.log("vertices:", "${pnl3Vertices.size} != ${cnl3Vertices.size}")

                //assuming they are same size?
                (1 until pnl3Vertices.size).forEach { idx ->
                    val normal = Vector3(pnl3Vertices[idx - 1])
                    modelBuilder.part("id${idx}", GL20.GL_TRIANGLES, attr, Material(TextureAttribute.createDiffuse(assets[TextureAssets.CaveWall]))).rect(
                            pnl3Vertices[idx - 1], cnl3Vertices[idx - 1], cnl3Vertices[idx], pnl3Vertices[idx], normal.crs(cnl3Vertices[idx])
                    )
                }
            }
        }
        //top wall 2
        (23 .. 30).forEach { xIdx ->

            val xIdxNoise = Probability(0, 10).getValue() / 100f
            val zIdxNoise = 1f + Probability(0, 10).getValue() / 100f

            if (xIdx == 23)
                currNodeLine3 = NodeLine3(firstNode = Node3(position = Vector3(xIdx + xIdxNoise, 10f, 30.5f + zIdxNoise)), lastNode = Node3(position = Vector3(xIdx + xIdxNoise, 110f, 30.5f + zIdxNoise)), lineNoise = wallNoise)
            else {
                prevNodeLine3 = currNodeLine3
                currNodeLine3 = NodeLine3(firstNode = Node3(position = Vector3(xIdx + xIdxNoise, 10f, 30.5f + zIdxNoise)), lastNode = Node3(position = Vector3(xIdx + xIdxNoise, 110f, 30.5f + zIdxNoise)), lineNoise = wallNoise)

                val pnl3Vertices = prevNodeLine3.getPositions()
                val cnl3Vertices = currNodeLine3.getPositions()

                if (pnl3Vertices.size != cnl3Vertices.size) Gdx.app.log("vertices:", "${pnl3Vertices.size} != ${cnl3Vertices.size}")

                //assuming they are same size?
                (1 until pnl3Vertices.size).forEach { idx ->
                    val normal = Vector3(pnl3Vertices[idx - 1])
                    modelBuilder.part("id${idx}", GL20.GL_TRIANGLES, attr, Material(TextureAttribute.createDiffuse(assets[TextureAssets.CaveWall]))).rect(
                            pnl3Vertices[idx - 1], cnl3Vertices[idx - 1], cnl3Vertices[idx], pnl3Vertices[idx], normal.crs(cnl3Vertices[idx])
                    )
                }
            }
        }
        modelWalls = modelBuilder.end()
        modelWallsInstance = ModelInstance(modelWalls, 0f, 10f, 0f)

        currentModelPosition = lookAt + Vector3(0f, 30f, 0f)

        modelEntity = G3dModelLoader(UBJsonReader()).loadModel(Gdx.files.getFileHandle("models/pc_model_Walking.g3db", Files.FileType.Internal))

        modelEntity.animations.first().id = "Walking"

        val leftTurnModelEntity = G3dModelLoader(UBJsonReader()).loadModel(Gdx.files.getFileHandle("models/pc_model_Left Turn.g3db", Files.FileType.Internal))
        val rightTurnModelEntity = G3dModelLoader(UBJsonReader()).loadModel(Gdx.files.getFileHandle("models/pc_model_Right Turn.g3db", Files.FileType.Internal))
        val stopWalkModelEntity = G3dModelLoader(UBJsonReader()).loadModel(Gdx.files.getFileHandle("models/pc_model_Stop Walking.g3db", Files.FileType.Internal))

        leftTurnModelEntity.animations.first().id = "LeftTurn"
        modelEntity.animations.add(leftTurnModelEntity.animations[0])

        rightTurnModelEntity.animations.first().id = "RightTurn"
        modelEntity.animations.add(rightTurnModelEntity.animations[0])

        stopWalkModelEntity.animations.first().id = "StopWalking"
        modelEntity.animations.add(stopWalkModelEntity.animations[0])

        stoppingAnimationDuration = stopWalkModelEntity.animations.first().duration
        stoppingAnimationDistance = (stopWalkModelEntity.animations.first().nodeAnimations[1].translation.last().value.z -
                stopWalkModelEntity.animations.first().nodeAnimations[1].translation.first().value.z ) * .1f

        modelEntityInstance = ModelInstance(modelEntity, currentModelPosition)
        modelEntityInstance.transform.rotate(Vector3(1f, 0f, 0f), 90f )
        modelEntityInstance.transform.scale(.1f, .1f, .1f)

        walkingAnimationDuration = modelEntityInstance.animations.first().duration
        walkingAnimationDistance = (modelEntityInstance.animations.first().nodeAnimations[1].translation.last().value.z -
            modelEntityInstance.animations.first().nodeAnimations[1].translation.first().value.z ) * .1f
/*
        class AL : AnimationListener {
            override fun onEnd(animation: AnimationController.AnimationDesc?) {
            }

            override fun onLoop(animation: AnimationController.AnimationDesc?) {
                modelEntityInstance.transform.trn(Vector3(0f, walkingAnimationDistance * sin(currentAnimationDirection.radians()), 0f))
                currentModelPosition += Vector3(0f, walkingAnimationDistance * sin(currentAnimationDirection.radians()), 0f)
            }
        }
*/
        animationController = AnimationController(modelEntityInstance)
//        animationController.setAnimation("Walking",0)

        wallEnvironment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.1f, 0.1f, 0.5f, 0.5f))
        wallEnvironment.add(spotLight)
        wallEnvironment.add(dirLight)
        floorEnvironment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.1f, 0.5f, 0.5f))
        floorEnvironment.add(spotLight)
        floorEnvironment.add(dirLight)
        modelEnvironment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 0.5f))
        modelEnvironment.add(spotLight)
        modelEnvironment.add(dirLight)
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
        gameCamera.viewportWidth = width.toFloat()
        gameCamera.viewportHeight = height.toFloat()
        menuCamera.viewportWidth = width.toFloat()
        menuCamera.viewportHeight = height.toFloat()
    }

    override fun dispose() {
        assets.dispose()
        modelWalls.dispose()
        modelFloors.dispose()
    }
}