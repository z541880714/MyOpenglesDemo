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

class RectRender() : GLSurfaceView.Renderer {


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

    private var vertShaderPath = "shader/rect.vert"

    private var fragShaderPath = "shader/rect.frag"
    private var programId = 0

    constructor(
        vertPath: String = "shader/rect.vert",
        fragPath: String = "shader/rect.frag"
    ) : this() {
        this.vertShaderPath = vertPath
        this.fragShaderPath = fragPath
    }


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        programId =
            zglCreateProgramIdFromAssets(appContext, vertShaderPath, fragShaderPath).programId
        // 应用GL程序
        // Use the GL program

        textureId = appContext.assets.open("images/image_2.jpg").use {
            zglGenAndBindTexture(it)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glUseProgram(programId)

        // 清屏
        // Clear the screen
//        glClear(GL_COLOR_BUFFER_BIT)

        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 8, vertexBuffer)
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 8, mTexVertexBuffer)


        // 绘制
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, mVertexIndexBuffer)
    }
}