package org.river.exertion.gdx3d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.GdxRuntimeException

class BasicShader : Shader {
    /*******************************************************************************
     * Copyright 2011 See AUTHORS file.
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *   http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     ******************************************************************************/

    /**
     * See: https://xoppa.github.io/blog/creating-a-shader-with-libgdx/
     * @author Xoppa -- Java Version
     * https://github.com/xoppa/blog/blob/master/tutorials/src/com/xoppa/blog/libgdx/g3d/createshader/step6/TestShader.java
     * exertionRiver -- Kotlin version
     */

    lateinit var program : ShaderProgram
    lateinit var camera : Camera
    lateinit var context : RenderContext
    var u_projTrans: Int = 0
    var u_color: Int = 0

    //https://stackoverflow.com/questions/46511824/how-do-you-incorporate-per-pixel-lighting-in-shaders-with-libgdx
    override fun init() {
        //val vert = Gdx.files.internal("data/basicVertexShader.glsl").readString()
        //val frag = Gdx.files.internal("data/basicFragmentShader.glsl").readString()
        val vert = DefaultShader.getDefaultVertexShader()
        val frag = DefaultShader.getDefaultFragmentShader()
        program = ShaderProgram(vert, frag)
        if (!program.isCompiled())
            throw GdxRuntimeException(program.getLog())
        u_projTrans = program.getUniformLocation("u_projTrans")
        u_color = program.getUniformLocation("u_color")
    }

    override fun dispose() {
        program.dispose()
    }

    override fun begin(camera : Camera, context : RenderContext) {
        this.camera = camera
        this.context = context

        program.bind()
        program.setUniformMatrix(u_projTrans, camera.combined)
        program.setUniformf(u_color, Color.BLUE)
        context.setDepthTest(GL20.GL_LEQUAL)
        context.setCullFace(GL20.GL_BACK)
    }

    override fun end() {
        //deprecated -- program.end()
    }

    override fun render(renderable: Renderable?) {
        if (renderable != null) {
            program.setUniformMatrix(u_projTrans, renderable.worldTransform)
            renderable.meshPart.render(program)
        }
    }

    override fun compareTo(other: Shader?): Int {
        return 0
    }

    override fun canRender(instance: Renderable?): Boolean {
        return true
    }
}