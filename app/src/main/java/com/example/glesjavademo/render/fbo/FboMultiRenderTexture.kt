package com.example.glesjavademo.render.fbo

import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLES30.*
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.glesjavademo.render.TextureRender
import com.example.glesjavademo.util.zglBindTexture
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 多重渲染目标,
 */
class FboMultiRenderTexture : GLSurfaceView.Renderer {

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
    val renderIds = IntArray(4)
    var query = IntBuffer.allocate(2)
    val fboId get() = intBuffer[0]

    val textureRenderer =
        TextureRender(vertPath = "shader/mrtTexture.vert", fragPath = "shader/mrtTexture.frag")

    fun initFbo(width: Int, height: Int) {
//        glGetIntegerv(GL_FRAMEBUFFER_BINDING, query)
//        Log.i("log_zc", "FboMultiRenderTarget-> onSurfaceCreated: fboId:${query[0]}, 2:${query[1]}")
        glGenFramebuffers(1, intBuffer)
        glBindFramebuffer(GL_FRAMEBUFFER, fboId)
        //查询当前 帧缓存 的状态
//        glGetIntegerv(GL_FRAMEBUFFER_BINDING, query)
//        Log.i("log_zc", "FboMultiRenderTarget-> onSurfaceCreated: fboId:${query[0]}, 2:${query[1]}")
        //创建 颜色附加纹理.
        glGenTextures(4, ColorTexIds)
        for (i in 0..3) {
            zglBindTexture(ColorTexIds[i], null, width, height)
            //将生成的 纹理 绑定到 附着点..
            glFramebufferTexture2D(
                GL_FRAMEBUFFER,
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
        textureRenderer.onSurfaceCreated(gl, config)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        Log.i("log_zc", "FboMultiRenderTarget-> onSurfaceChanged: 333!!!")
        this.width = width
        this.height = height
        textureRenderer.onSurfaceChanged(gl, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        if (fboId == 0) {
            initFbo(this.width, this.height)
        }
        glBindFramebuffer(GL_FRAMEBUFFER, fboId)
        // 为渲染指定 颜色附着.
        glDrawBuffers(4, attachment, 0)
        drawFboTexture(gl)

        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        blitTextures();
    }

    private fun blitTextures() {
        // 位块传输 , 准备从 帧缓存 传入 系统窗口的 surface 中.
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
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

    private fun drawFboTexture(gl: GL10?) {
        textureRenderer.onDrawFrame(gl)
    }
}