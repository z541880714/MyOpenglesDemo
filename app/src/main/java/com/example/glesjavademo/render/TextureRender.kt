package com.example.glesjavademo.render

import android.opengl.GLES30.*
import android.opengl.GLSurfaceView
import com.example.glesjavademo.appContext
import com.example.glesjavademo.util.zglCreateProgramIdFromAssets
import com.example.glesjavademo.util.zglGenTexture
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TextureRender : GLSurfaceView.Renderer {


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

    private var vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(POSITION_VERTEX.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
    var mTexVertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(TEX_VERTEX.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    private var mVertexIndexBuffer: ShortBuffer =
        ByteBuffer.allocateDirect(VERTEX_INDEX.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        //传入指定的坐标数据
        //传入指定的坐标数据
        vertexBuffer.put(POSITION_VERTEX)
        vertexBuffer.position(0)

        mTexVertexBuffer.put(TEX_VERTEX)
        mTexVertexBuffer.position(0)

        mVertexIndexBuffer.put(VERTEX_INDEX)
        mVertexIndexBuffer.position(0)


        val programId =
            zglCreateProgramIdFromAssets(appContext, "shader/vbo.vert", "shader/vbo.frag").programId
        // 应用GL程序
        // Use the GL program
        glUseProgram(programId)

        textureId = appContext.assets.open("images/image_2.jpg").use {
            zglGenTexture(it)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        // 清屏
        // Clear the screen
        glClear(GL_COLOR_BUFFER_BIT)


        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 8, vertexBuffer)
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 8, mTexVertexBuffer)


        glActiveTexture(GL_TEXTURE0)
        //绑定纹理
        //绑定纹理
        glBindTexture(GL_TEXTURE_2D, textureId)

        // 绘制
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, mVertexIndexBuffer)
    }
}