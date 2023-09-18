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


/**
 * 用来 进行 源缓冲区  进行 颜色混合.. 达到 alpha 主帧较少的效果
 */
class RectRender() : GLSurfaceView.Renderer {


    private val POSITION_VERTEX = floatArrayOf(
        1f, 1f,
        -1f, 1f,
        -1f, -1f,

        -1f, -1f,
        1f, -1f,
        1f, 1f,
    )


    private var vertexBuffer = POSITION_VERTEX.toFloatBuffer()

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
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glUseProgram(programId)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // 清屏
        // Clear the screen
//        glClear(GL_COLOR_BUFFER_BIT)

        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 8, vertexBuffer)

        // 绘制
        glDrawArrays(GL_TRIANGLES, 0, 6)
    }
}