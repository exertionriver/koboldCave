package org.river.exertion.demos.gdx3d

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute.createDiffuse
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.glutils.IndexData
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.VertexData
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.StretchViewport
import ktx.app.KtxScreen
import ktx.ashley.get
import ktx.graphics.use
import ktx.scene2d.*
import org.river.exertion.*
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.component.entity.character.CharacterPlayerCharacter
import org.river.exertion.ecs.component.entity.location.LocationCave
import org.river.exertion.ecs.system.action.SystemManager
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
    lateinit var meshInstance : Mesh
    val environment = Environment()

    var lightX = -1f
    var lightY = -0.8f
    var lightZ = -0.2f

    val x = 0f //playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid.position.x
    val y = 0f //playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid.position.y
    val z = 0f

    val originPosition = Vector3(x, y, z)

    val initLight = DirectionalLight().set(1.0f, 1.0f, 1.0f, lightX, lightY, lightZ)

    lateinit var modelGLLine : Model
    lateinit var modelNodeLine : Model
    lateinit var modelPoint : Model

    @Suppress("NewApi")
    override fun render(delta: Float) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        InputHandler.handleInput(gameCamera, originPosition)

        when {
            Gdx.input.isKeyJustPressed(Input.Keys.UP) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.FORWARD }
            Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.BACKWARD }
            Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.LEFT }
            Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.RIGHT }

            Gdx.input.isKeyJustPressed(Input.Keys.T) -> { lightX += 0.1f ; initLight.set(1.0f, 1.0f, 1.0f, lightX, lightY, lightZ) }
            Gdx.input.isKeyJustPressed(Input.Keys.G) -> { lightY += 0.1f ; initLight.set(1.0f, 1.0f, 1.0f, lightX, lightY, lightZ) }
            Gdx.input.isKeyJustPressed(Input.Keys.B) -> { lightZ += 0.1f ; initLight.set(1.0f, 1.0f, 1.0f, lightX, lightY, lightZ) }
            Gdx.input.isKeyJustPressed(Input.Keys.Y) -> { lightX -= 0.1f ; initLight.set(1.0f, 1.0f, 1.0f, lightX, lightY, lightZ) }
            Gdx.input.isKeyJustPressed(Input.Keys.H) -> { lightY -= 0.1f ; initLight.set(1.0f, 1.0f, 1.0f, lightX, lightY, lightZ) }
            Gdx.input.isKeyJustPressed(Input.Keys.N) -> { lightZ -= 0.1f ; initLight.set(1.0f, 1.0f, 1.0f, lightX, lightY, lightZ) }
        }

        gameCamera.update()
        gameBatch.begin(gameCamera)
        gameBatch.render(modelInstance, environment)
        gameBatch.render(modelLineInstance, environment)
        gameBatch.render(modelGLLineInstance, environment)
        gameBatch.end()

//        meshInstance.render(shader, GL20.GL_LINES)
//        menuCamera.update()
//        menuBatch.projectionMatrix = menuCamera.combined

//        menuStage.draw()
//        menuStage.act()

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
        val point = Point(x, y)
        Render.initRender(gameCamera, Node(position = point), Render.cameraAngle)
        Render.initRender(menuCamera, Node(position = point), Render.cameraAngle)

        Scene2DSkin.defaultSkin = Skin(Gdx.files.internal("skin/clean-crispy-ui.json"))

//        val firstLinePoint = Vector3(x, y+1f, z+2f)
//        val secondLinePoint = Vector3(x+50f, y+40f, z+30f)
        val firstLinePoint = Vector3(10f, 20f, 30f)
        val secondLinePoint = Vector3(10f, 50f, 70f)

        modelPoint = modelBuilder.createSphere(5f, 5f, 5f, 20, 20, Material(createDiffuse(Color.BLUE)), (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
//        modelInstance = ModelInstance(modelPoint, x, y, z)
//        modelInstance = ModelInstance(modelPoint, -1f, -4f, 3f)
        modelInstance = ModelInstance(modelBuilder.createLineGrid(10, 10, 10f, 10f, Material(createDiffuse(Color.BLUE)), (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()), x, y, z)

        meshBuilder.begin((VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        meshBuilder.part("id1", GL20.GL_LINES)
        meshBuilder.line(firstLinePoint, secondLinePoint)
        meshInstance = meshBuilder.end()

        modelBuilder.begin()
        modelBuilder.part("id1", meshInstance, GL20.GL_LINES, Material(createDiffuse(Color.RED)))
        modelGLLine = modelBuilder.end()
        modelGLLineInstance = ModelInstance(modelGLLine, x, y, z)

        val nodeLine = NodeLine3(firstNode = Node3(position = firstLinePoint), lastNode = Node3(position = secondLinePoint), lineNoise = 100)
        val lines = nodeLine.getLines()

        meshBuilder.begin((VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        meshBuilder.part("id2", GL20.GL_LINES)
        lines.forEach { line3 ->
            meshBuilder.line(line3.first, line3.second)
        }
        meshInstance = meshBuilder.end()

        modelBuilder.begin()
        modelBuilder.part("id2", meshInstance, GL20.GL_LINES, Material(createDiffuse(Color.GREEN)))
        modelNodeLine = modelBuilder.end()
        modelLineInstance = ModelInstance(modelNodeLine, x, y, z)


/*        meshBuilder.begin((VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        meshBuilder.part("id2", GL20.GL_TRIANGLES)
        meshBuilder.triangle(Vector3(x, y+1f, z+2f), Vector3(x, y-1f, z-2f), Vector3(x-10f, y-10f, z-10f))
        meshInstance = meshBuilder.end()

        modelBuilder.begin()
        modelBuilder.part("id2", meshInstance, GL20.GL_TRIANGLES, Material(createDiffuse(Color.YELLOW)))
        val modelTriangle = modelBuilder.end()
        modelTriangleInstance = ModelInstance(modelTriangle, x, y, z)
*/
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 0.4f))
        environment.add(initLight)
    }

    fun buildMesh() : Mesh {
        val verts = FloatArray(20)
        var i = 0

        verts[i++] = -1f // x1
        verts[i++] = -1f // y1
        verts[i++] = 0f
        verts[i++] = 0f // u1
        verts[i++] = 0f // v1

        verts[i++] = 1f // x2
        verts[i++] = -1f // y2
        verts[i++] = 0f
        verts[i++] = 1f // u2
        verts[i++] = 0f // v2

        verts[i++] = 1f // x3
        verts[i++] = 1f // y2
        verts[i++] = 0f
        verts[i++] = 1f // u3
        verts[i++] = 1f // v3

        verts[i++] = -1f // x4
        verts[i++] = 1f // y4
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

    }
}