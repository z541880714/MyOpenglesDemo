package com.example.glesjavademo.render

import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLES30.GL_COLOR_ATTACHMENT0
import android.opengl.GLES30.GL_COLOR_ATTACHMENT1
import android.opengl.GLES30.GL_COLOR_ATTACHMENT2
import android.opengl.GLES30.GL_COLOR_ATTACHMENT3
import android.opengl.GLES30.GL_COLOR_BUFFER_BIT
import android.opengl.GLES30.GL_DRAW_FRAMEBUFFER
import android.opengl.GLES30.GL_FLOAT
import android.opengl.GLES30.GL_FRAMEBUFFER
import android.opengl.GLES30.GL_FRAMEBUFFER_BINDING
import android.opengl.GLES30.GL_FRAMEBUFFER_COMPLETE
import android.opengl.GLES30.GL_LINEAR
import android.opengl.GLES30.GL_READ_FRAMEBUFFER
import android.opengl.GLES30.GL_TEXTURE_2D
import android.opengl.GLES30.GL_TRIANGLES
import android.opengl.GLES30.glBlitFramebuffer
import android.opengl.GLES30.glCheckFramebufferStatus
import android.opengl.GLES30.glClear
import android.opengl.GLES30.glDrawBuffers
import android.opengl.GLES30.glEnableVertexAttribArray
import android.opengl.GLES30.glFramebufferTexture2D
import android.opengl.GLES30.glGenFramebuffers
import android.opengl.GLES30.glGenTextures
import android.opengl.GLES30.glGetIntegerv
import android.opengl.GLES30.glReadBuffer
import android.opengl.GLES30.glUseProgram
import android.opengl.GLES30.glVertexAttribPointer
import android.opengl.GLES30.glViewport
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.glesjavademo.appContext
import com.example.glesjavademo.util.zglBindTexture
import com.example.glesjavademo.util.zglCreateProgramIdFromAssets
import com.example.glesjavademo.util.zglGenTexture
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * 多重渲染目标,
 */
class FboMultiRenderTarget : GLSurfaceView.Renderer {

    var width = 0
    var height = 0

    val attachment = intArrayOf(
        GL_COLOR_ATTACHMENT0,
        GL_COLOR_ATTACHMENT1,
        GL_COLOR_ATTACHMENT2,
        GL_COLOR_ATTACHMENT3,
    )
    var intBuffer = IntBuffer.allocate(2)
    val ColorTexIds = IntBuffer.allocate(4)
    var query = IntBuffer.allocate(2)
    val fboId get() = intBuffer[0]

    var projectId = 0
    fun initFbo(width: Int, height: Int) {
        glGetIntegerv(GL_FRAMEBUFFER_BINDING, query)
        Log.i("log_zc", "FboMultiRenderTarget-> onSurfaceCreated: fboId:${query[0]}, 2:${query[1]}")
        glGenFramebuffers(1, intBuffer)
        glBindFramebuffer(GL_FRAMEBUFFER, fboId)
        glGetIntegerv(GL_FRAMEBUFFER_BINDING, query)
        Log.i("log_zc", "FboMultiRenderTarget-> onSurfaceCreated: fboId:${query[0]}, 2:${query[1]}")
        glGenTextures(4, ColorTexIds)
        for (i in 0..3) {
            zglBindTexture(ColorTexIds[i], null, width, height)
            glFramebufferTexture2D(
                GL_DRAW_FRAMEBUFFER,
                attachment[i],
                GL_TEXTURE_2D,
                ColorTexIds[i],
                0
            )
        }
        val ret = glCheckFramebufferStatus(GL_FRAMEBUFFER)
        Log.i("log_zc", "FboMultiRenderTarget-> initFbo: rec:${ret == GL_FRAMEBUFFER_COMPLETE}")
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val projectData =
            zglCreateProgramIdFromAssets(appContext, "shader/mrt.vert", "shader/mrt.frag")
        projectId = projectData.programId

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        Log.i("log_zc", "FboMultiRenderTarget-> onSurfaceChanged: 333!!!")
        this.width = width
        this.height = height
    }

    override fun onDrawFrame(gl: GL10?) {
        if (fboId == 0) {
            initFbo(this.width, this.height)
        }
        glBindFramebuffer(GL_FRAMEBUFFER, fboId)

        drawGeometry()

//        drawTexture()

        // 位块传输 , 准备从 帧缓存 传入 系统窗口的 surface 中.
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        blitTextures();
    }

    private fun blitTextures() {
        //从 fboId 中读取, 写入 窗口..
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fboId)

        //注意坐标 原点在左下角. .
        glReadBuffer(GL_COLOR_ATTACHMENT0);
        glBlitFramebuffer(
            0, 0, this.width, this.height,
            0, 0, this.width / 2, this.height / 2,
            GL_COLOR_BUFFER_BIT, GL_LINEAR
        )

        glReadBuffer(GL_COLOR_ATTACHMENT1);
        glBlitFramebuffer(
            0, 0, this.width, this.height,
            this.width / 2, 0, this.width, this.height / 2,
            GL_COLOR_BUFFER_BIT, GL_LINEAR
        )

        glReadBuffer(GL_COLOR_ATTACHMENT2);
        glBlitFramebuffer(
            0, 0, this.width, this.height,
            0, this.height / 2, this.width / 2, this.height,
            GL_COLOR_BUFFER_BIT, GL_LINEAR
        )

        glReadBuffer(GL_COLOR_ATTACHMENT3);
        glBlitFramebuffer(
            0, 0, this.width, this.height,
            this.width / 2, this.height / 2, this.width, this.height,
            GL_COLOR_BUFFER_BIT, GL_LINEAR
        )
    }

    private fun drawGeometry() {
        val vVertices = floatArrayOf(
            -1.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f
        )
        val fBuffer =
            ByteBuffer.allocateDirect(3 * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
                .apply {
                    put(vVertices)
                    position(0)
                }

        val indices = intArrayOf(0, 1, 2, 0, 2, 3)
        val iBuffer =
            ByteBuffer.allocateDirect(6 * 4).order(ByteOrder.nativeOrder()).asIntBuffer()
                .apply {
                    put(indices)
                    position(0)
                }
        glViewport(0, 0, this.width, this.height)
        glClear(GL_COLOR_BUFFER_BIT)
        glUseProgram(projectId)

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, fBuffer)
        glEnableVertexAttribArray(0)


        //绑定 附着点.. 让 片段着色器 输出 到对应的附着点...
        glDrawBuffers(4, attachment, 0)
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, iBuffer)

    }


    private val vertexData = floatArrayOf(
        -1f, -1f, 0f, 1f,    // x, y , u, v  顶点坐标 和纹理坐标 一起...
        -1f, 1f, 0f, 0f,
        1f, 1f, 1f, 0f,
        1f, -1f, 1f, 1f,
    )

    private val indexData = intArrayOf(0, 1, 2, 0, 2, 3)

    private val vertexBuffer = ByteBuffer.allocateDirect(vertexData.size * 4)
        .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
            put(vertexData)
            position(0)
        }

    val indexDataBuffer = ByteBuffer.allocateDirect(indexData.size * 4)
        .order(ByteOrder.nativeOrder()).asIntBuffer().apply {
            put(indexData)
            position(0)
        }

    private var imageTexture = 0


    private fun drawTexture() {
        glViewport(0, 0, this.width, this.height)
        glClear(GL_COLOR_BUFFER_BIT)
        glUseProgram(projectId)

        glVertexAttribPointer(0, 4, GL_FLOAT, false, 16, vertexBuffer)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 16, 0)
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 16, 8)

        imageTexture = appContext.assets.open("images/image_2.jpg").use {
            zglGenTexture(it)
        }
        glActiveTexture(GL_TEXTURE0)

        //绑定 附着点.. 让 片段着色器 输出 到对应的附着点...
        glDrawBuffers(4, attachment, 0)
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, indexDataBuffer)
    }
}