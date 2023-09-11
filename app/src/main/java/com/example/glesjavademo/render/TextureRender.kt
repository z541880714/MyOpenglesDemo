package com.example.glesjavademo.render

import android.opengl.GLES30.*
import android.opengl.GLSurfaceView
import com.example.glesjavademo.appContext
import com.example.glesjavademo.util.toFloatBuffer
import com.example.glesjavademo.util.toShortBuffer
import com.example.glesjavademo.util.zglCreateProgramIdFromAssets
import com.example.glesjavademo.util.zglGenAndBindTexture
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TextureRender() : GLSurfaceView.Renderer {

    private var sWidth = 0
    private var sHeight = 0

    private val POSITION_VERTEX = floatArrayOf(
        1f, 1f,
        -1f, 1f,
        -1f, -1f,
        1f, -1f,
    )

    /**
     * 纹理坐标
     * (s,t)
     */
    private val TEX_VERTEX = floatArrayOf(
        1f, 0f,  //纹理坐标V0
        0f, 0f,  //纹理坐标V1
        0f, 1f,  //纹理坐标V3
        1f, 1f //纹理坐标V4
    )

    /**
     * 索引
     */
    private val VERTEX_INDEX = shortArrayOf(
        0, 1, 2,  //V0,V1,V2 三个顶点组成一个三角形
        0, 2, 3
    )

    private var textureId = 0

    private var vertexBuffer = POSITION_VERTEX.toFloatBuffer()
    var mTexVertexBuffer: FloatBuffer = TEX_VERTEX.toFloatBuffer()
    private var mVertexIndexBuffer: ShortBuffer = VERTEX_INDEX.toShortBuffer()


    private var vertShaderPath = "shader/vbo.vert"

    private var fragShaderPath = "shader/vbo.frag"

    private var programId = 0

    constructor(
        vertPath: String = "shader/vbo.vert",
        fragPath: String = "shader/vbo.frag",
        textureId: Int = 0,
    ) : this() {
        this.vertShaderPath = vertPath
        this.fragShaderPath = fragPath
        this.textureId = textureId
    }

    /**
     * 外部 设置的 textureId
     */
    fun setTextureId(textureId: Int) {
        this.textureId = textureId
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        programId =
            zglCreateProgramIdFromAssets(appContext, vertShaderPath, fragShaderPath).programId
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.sWidth = width
        this.sHeight = height
        glViewport(0, 0, sWidth, sHeight)
    }

    override fun onDrawFrame(gl: GL10?) {
        glUseProgram(programId)
        // 清屏
        // Clear the screen
        glClear(GL_COLOR_BUFFER_BIT)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 8, vertexBuffer)
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 8, mTexVertexBuffer)


        if (textureId == 0) initTexture()
        glActiveTexture(GL_TEXTURE0)
        //绑定纹理
        //绑定纹理
        glBindTexture(GL_TEXTURE_2D, textureId)

        // 绘制
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, mVertexIndexBuffer)
    }

    private fun initTexture() {
        if (this.textureId > 0) return
        textureId = appContext.assets.open("images/image_2.jpg").use {
            zglGenAndBindTexture(it)
        }
    }
}