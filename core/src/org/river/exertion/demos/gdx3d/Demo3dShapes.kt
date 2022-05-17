package org.river.exertion.demos.gdx3d

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute.createDiffuse
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.StretchViewport
import ktx.app.KtxScreen
import ktx.ashley.get
import ktx.graphics.use
import ktx.scene2d.*
import org.river.exertion.*
import org.river.exertion.assets.TextureAssets
import org.river.exertion.assets.get
import org.river.exertion.assets.load
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.entity.character.CharacterPlayerCharacter
import org.river.exertion.ecs.entity.location.LocationCave
import org.river.exertion.ecs.system.SystemManager
import org.river.exertion.geom.node.Node
import org.river.exertion.geom.node.Node3
import org.river.exertion.geom.node.nodeMesh.NodeLine3
import org.river.exertion.geom.node.nodeMesh.NodeRoom
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh


class Demo3dShapes(private val menuBatch: Batch,
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

    val engine = PooledEngine().apply { SystemManager.init(this) }
    val cave = LocationCave.instantiate(engine, menuStage, "spookyCave", nodeRoomMesh)
    val playerCharacter = CharacterPlayerCharacter.instantiate(engine, menuStage, location = cave, camera = null)

//    val controlAreaCamera = OrthographicCamera()
    val gameAreaViewport = StretchViewport(Game.initViewportWidth, Game.initViewportHeight, gameCamera)

    val sdc = ShapeDrawerConfig(menuBatch)
    val drawer = sdc.getDrawer()

    val modelBuilder = ModelBuilder()
    val meshBuilder = MeshBuilder()

    lateinit var modelInstance : ModelInstance
    lateinit var modelLineInstance : ModelInstance
    lateinit var modelGLLineInstance : ModelInstance
    lateinit var boxInstance : ModelInstance
    lateinit var meshInstance : Mesh
    val environment = Environment()

    var lightX = -1f
    var lightY = -20f
    var lightZ = -0.2f

    val x = 0f //playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid.position.x
    val y = 0f //playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid.position.y
    val z = 0f

    val originPosition = Vector3(x, y, z)
    val camPosition = Vector3(x, y, z+30f)

    val initLight = DirectionalLight().set(0.5f, 0.5f, 1.0f, lightX, lightY, lightZ)

    lateinit var modelGLLine : Model
    lateinit var modelNodeLine : Model
    lateinit var modelPoint : Model
    lateinit var boxModel : Model

    val renderMesh = buildMesh()

    val shaderProgram = ShaderProgram(vertexShader(), fragmentShader())

    @Suppress("NewApi")
    override fun render(delta: Float) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        InputHandler.handleInput(gameCamera, originPosition)

        when {
            Gdx.input.isKeyJustPressed(Input.Keys.UP) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.FORWARD }
            Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.BACKWARD }
            Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.LEFT }
            Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.RIGHT }

            Gdx.input.isKeyJustPressed(Input.Keys.T) -> { lightX += 10f ; initLight.setDirection(lightX, lightY, lightZ) }
            Gdx.input.isKeyJustPressed(Input.Keys.G) -> { lightY += 10f ; initLight.setDirection(lightX, lightY, lightZ) }
            Gdx.input.isKeyJustPressed(Input.Keys.B) -> { lightZ += 10f ; initLight.setDirection(lightX, lightY, lightZ) }
            Gdx.input.isKeyJustPressed(Input.Keys.Y) -> { lightX -= 10f ; initLight.setDirection(lightX, lightY, lightZ) }
            Gdx.input.isKeyJustPressed(Input.Keys.H) -> { lightY -= 10f ; initLight.setDirection(lightX, lightY, lightZ)}
            Gdx.input.isKeyJustPressed(Input.Keys.N) -> { lightZ -= 10f ; initLight.setDirection(lightX, lightY, lightZ) }
        }

        gameCamera.update()
        gameBatch.begin(gameCamera)
//        gameBatch.render(modelInstance, environment)
//        gameBatch.render(modelLineInstance, environment)
//        gameBatch.render(modelGLLineInstance, environment)
        gameBatch.render(boxInstance, environment)
        gameBatch.end()

//        meshInstance.render(shader, GL20.GL_LINES)
//        menuCamera.update()
//        menuBatch.projectionMatrix = menuCamera.combined

//        menuStage.draw()
//        menuStage.act()

        assets[TextureAssets.Kobold].bind();
        shaderProgram.bind()
        shaderProgram.setUniformMatrix("u_projTrans", gameCamera.projection);
        shaderProgram.setUniformi("u_texture", 0);
        renderMesh.render(shaderProgram, GL20.GL_TRIANGLES);

        menuBatch.use {
            font.drawLabel(menuBatch, Point(300f, 200f), "${playerCharacter[ActionMoveComponent.mapper]!!.currentNode}\n${playerCharacter[ActionMoveComponent.mapper]!!.currentNodeLink}\n" +
                    "nodeRoom:${playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.uuid}\nlength:${playerCharacter[ActionMoveComponent.mapper]!!.currentNodeLink.getDistance(nodeRoomMesh.nodesMap.keys)}\n" +
                    "occupiedNodes:${cave[LocationCave.mapper]!!.nodeRoomMesh.numOccupiedNodes()}/${cave[LocationCave.mapper]!!.nodeRoomMesh.nodesMap.size}", RenderPalette.ForeColors[1])
        }

        engine.update(delta)

    }

    override fun hide() {
    }

    override fun show() {
        Render.initRender(gameCamera, camPosition, originPosition)

        val point = Point(x, y)
        Render.initRender(menuCamera, Node(position = point), Render.cameraAngle)

        Scene2DSkin.defaultSkin = Skin(Gdx.files.internal("skin/clean-crispy-ui.json"))
        TextureAssets.values().forEach { assets.load(it) }
        assets.finishLoading()
//        val firstLinePoint = Vector3(x, y+1f, z+2f)
//        val secondLinePoint = Vector3(x+50f, y+40f, z+30f)
        val firstLinePoint = Vector3(0f, 0f, 0f)
        val secondLinePoint = Vector3(0f, 0f, 30f)

        modelPoint = modelBuilder.createSphere(5f, 5f, 5f, 20, 20, Material(createDiffuse(Color.BLUE)), (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        modelInstance = ModelInstance(modelBuilder.createLineGrid(10, 10, 10f, 10f, Material(createDiffuse(Color.BLUE)), (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()), x, y, z)
/*
        meshBuilder.begin((VertexAttributes.Usage.Position).toLong())
        meshBuilder.part("id1", GL20.GL_LINES)
        meshBuilder.line(firstLinePoint, secondLinePoint)
        meshInstance = meshBuilder.end()

        modelBuilder.begin()
        modelBuilder.part("id1", meshInstance, GL20.GL_LINES, Material(createDiffuse(Color.RED)))
        modelGLLine = modelBuilder.end()
        modelGLLineInstance = ModelInstance(modelGLLine, x, y, z)
*/
        val firstNodeLine = NodeLine3(firstNode = Node3(position = firstLinePoint), lastNode = Node3(position = secondLinePoint), lineNoise = Vector3(30f, 0f, 0f))
        val firstVertices = firstNodeLine.getPositions()

        val secondNodeLine = NodeLine3(firstNode = Node3(position = firstLinePoint.add(1f, 1f, 1f)), lastNode = Node3(position = secondLinePoint.add(1f, 1f, 1f)), lineNoise = Vector3(100f, 0f, 0f))
        val secondVertices = secondNodeLine.getPositions()
/*
        val bothVertices = FloatArray(firstVertices.size + secondVertices.size)
        firstVertices.forEachIndexed { idx, float -> bothVertices[idx] = float }
        secondVertices.forEachIndexed { idx, float -> bothVertices[idx + firstVertices.size] = float }
*/
        val attr = (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong()
        val attr2 = (VertexAttributes.Usage.Position or VertexAttributes.Usage.TextureCoordinates).toLong()

        meshBuilder.begin(attr)
        meshBuilder.part("id2", GL20.GL_TRIANGLES)
//        meshBuilder.add .addMesh(bothVertices, indices)
        meshInstance = meshBuilder.end()

        modelBuilder.begin()
        (1 until firstVertices.size).forEach { idx ->
            val normal = Vector3(firstVertices[idx - 1])
            modelBuilder.part("id${idx}", GL20.GL_TRIANGLES, attr, Material(TextureAttribute.createDiffuse(assets[TextureAssets.CaveWall]))).rect(
                    firstVertices[idx - 1], secondVertices[idx - 1], secondVertices[idx], firstVertices[idx], normal.crs(secondVertices[idx])
            )
        }

        modelNodeLine = modelBuilder.end()
        modelLineInstance = ModelInstance(modelNodeLine, x+5, y+5, z+5)

//https://stackoverflow.com/questions/27452192/libgdx-mapping-individual-textures-to-each-face-of-a-box-using-modelbuilder-cr
        modelBuilder.begin()
        modelBuilder.part("front", GL20.GL_TRIANGLES, attr, Material(TextureAttribute.createDiffuse(assets[TextureAssets.Kobold])))
                .rect(-2f, -2f, -2f, -2f, 2f, -2f, 2f, 2f, -2f, 2f, -2f, -2f, 0f, 0f, -1f)
        modelBuilder.part("back", GL20.GL_TRIANGLES, attr, Material(TextureAttribute.createDiffuse(assets[TextureAssets.Kobold])))
                .rect(-2f, 2f, 2f, -2f, -2f, 2f, 2f, -2f, 2f, 2f, 2f, 2f, 0f, 0f, 1f)
        modelBuilder.part("bottom", GL20.GL_TRIANGLES, attr, Material(TextureAttribute.createDiffuse(assets[TextureAssets.Kobold])))
                .rect(-2f, -2f, 2f, -2f, -2f, -2f, 2f, -2f, -2f, 2f, -2f, 2f, 0f, -1f, 0f)
        modelBuilder.part("top", GL20.GL_TRIANGLES, attr, Material(TextureAttribute.createDiffuse(assets[TextureAssets.Cave1])))
                .rect(-2f, 2f, -2f, -2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, -2f, 0f, 1f, 0f)
        modelBuilder.part("left", GL20.GL_TRIANGLES, attr, Material(TextureAttribute.createDiffuse(assets[TextureAssets.Cave1])))
                .rect(-2f, -2f, 2f, -2f, 2f, 2f, -2f, 2f, -2f, -2f, -2f, -2f, -1f, 0f, 0f)
        modelBuilder.part("right", GL20.GL_TRIANGLES, attr, Material(TextureAttribute.createDiffuse(assets[TextureAssets.Cave1])))
                .rect(2f, -2f, -2f, 2f, 2f, -2f, 2f, 2f, 2f, 2f, -2f, 2f, 1f, 0f, 0f)
        boxModel = modelBuilder.end()
        boxInstance = ModelInstance(boxModel, x, y, z)

//        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 0.4f))
        environment.add(initLight)
    }

    //https://gamefromscratch.com/libgdx-tutorial-part-12-using-glsl-shaders-and-creating-a-mesh/
    fun buildMesh() : Mesh {
        val verts = FloatArray(20)
        var i = 0

        verts[i++] = -50f // x1
        verts[i++] = -50f // y1
        verts[i++] = 0f
        verts[i++] = 0f // u1
        verts[i++] = 0f // v1

        verts[i++] = 50f // x2
        verts[i++] = -50f // y2
        verts[i++] = 0f
        verts[i++] = 1f // u2
        verts[i++] = 0f // v2

        verts[i++] = 50f // x3
        verts[i++] = 50f // y2
        verts[i++] = 0f
        verts[i++] = 1f // u3
        verts[i++] = 1f // v3

        verts[i++] = -50f // x4
        verts[i++] = 50f // y4
        verts[i++] = 0f
        verts[i++] = 0f // u4
        verts[i++] = 1f // v4

        val mesh = Mesh(true, 4, 0,  // static mesh with 4 vertices and no indices
                VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"))

        mesh.setVertices(verts)
        return mesh
    }

    fun vertexShader() = """attribute vec4 a_position;    
        attribute vec4 a_color;
        attribute vec2 a_texCoord0;
        uniform mat4 u_projTrans;
        varying vec4 v_color;varying vec2 v_texCoords;void main()                  
        {                            
           v_color = vec4(1, 1, 1, 1); 
           v_texCoords = a_texCoord0; 
           gl_Position =  u_projTrans * a_position;  
        }                            
        """

    fun fragmentShader() = """#ifdef GL_ES
        precision mediump float;
        #endif
        varying vec4 v_color;
        varying vec2 v_texCoords;
        uniform sampler2D u_texture;
        void main()                                  
        {                                            
          gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
        }"""

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
        modelGLLine.dispose()
        modelNodeLine.dispose()
        modelPoint.dispose()
        boxModel.dispose()

    }
}