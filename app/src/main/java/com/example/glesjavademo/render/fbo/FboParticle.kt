package com.example.glesjavademo.render.fbo

import android.opengl.GLES10
import android.opengl.GLES20
import android.opengl.GLES20.GL_COLOR_ATTACHMENT0
import android.opengl.GLES30
import android.opengl.GLES30.*
import android.opengl.GLES30.GL_COLOR_BUFFER_BIT
import android.opengl.GLES30.GL_DRAW_FRAMEBUFFER
import android.opengl.GLES30.GL_FRAMEBUFFER
import android.opengl.GLES30.GL_FRAMEBUFFER_BINDING
import android.opengl.GLES30.GL_LINEAR
import android.opengl.GLES30.GL_ONE_MINUS_SRC_ALPHA
import android.opengl.GLES30.GL_READ_FRAMEBUFFER
import android.opengl.GLES30.GL_SRC_ALPHA
import android.opengl.GLES30.GL_TEXTURE_2D
import android.opengl.GLES30.glBindFramebuffer
import android.opengl.GLES30.glBlitFramebuffer
import android.opengl.GLES30.glCheckFramebufferStatus
import android.opengl.GLES30.glDrawBuffers
import android.opengl.GLES30.glFramebufferTexture2D
import android.opengl.GLES30.glGenFramebuffers
import android.opengl.GLES30.glGenTextures
import android.opengl.GLES30.glGetIntegerv
import android.opengl.GLES30.glReadBuffer
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.glesjavademo.render.LinesRender
import com.example.glesjavademo.render.RectRender
import com.example.glesjavademo.util.FrameCounter
import com.example.glesjavademo.util.zglBindTexture
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.system.measureTimeMillis

class FboParticle : GLSurfaceView.Renderer {
    private var sw: Int = 0
    private var sh: Int = 0

    val linesRender = LinesRender()
    val rectRender = RectRender()

    val fboBuffers = IntArray(1)
    val textureIds = IntArray(1)
    val attachments = intArrayOf(
        GL_COLOR_ATTACHMENT0
    )

    private val frameCounter = FrameCounter()


    val fboId get() = fboBuffers[0]

    private val equaInt = intArrayOf(
        GL_FUNC_ADD, GLES20.GL_FUNC_SUBTRACT, GLES20.GL_FUNC_REVERSE_SUBTRACT
    )

    fun initFbo(width: Int, height: Int) {
        val query = IntBuffer.allocate(2)
        Log.i("log_zc", "FboMultiRenderTarget-> onSurfaceCreated: fboId:${query[0]}, 2:${query[1]}")
        glGenFramebuffers(1, fboBuffers, 0)
        GLES20.glBindFramebuffer(GL_FRAMEBUFFER, fboId)
        glGetIntegerv(GL_FRAMEBUFFER_BINDING, query)
        Log.i("log_zc", "FboMultiRenderTarget-> onSurfaceCreated: fboId:${query[0]}, 2:${query[1]}")
        glGenTextures(1, textureIds, 0)
        zglBindTexture(textureIds[0], null, width, height)
        glFramebufferTexture2D(
            GL_FRAMEBUFFER,
            attachments[0],
            GL_TEXTURE_2D,
            textureIds[0],
            0
        )
        val ret = glCheckFramebufferStatus(GL_FRAMEBUFFER)
        Log.i(
            "log_zc",
            "FboMultiRenderTarget-> initFbo: rec:${ret == GLES30.GL_FRAMEBUFFER_COMPLETE}"
        )
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        linesRender.onSurfaceCreated(gl, config)
        rectRender.onSurfaceCreated(gl, config)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        sw = width; sh = height
        linesRender.onSurfaceChanged(gl, width, height)
        rectRender.onSurfaceChanged(gl, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        if (fboId == 0) {
            initFbo(sw, sh)
        }
        glBindFramebuffer(GL_FRAMEBUFFER, fboId)
        glDrawBuffers(1, attachments, 0)

        GLES20.glEnable(GL_BLEND)


        GLES10.glBlendFunc(GL_SRC_ALPHA, GL_DST_ALPHA);
        val t1 = measureTimeMillis {
            linesRender.onDrawFrame(gl)
        }

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        val t2 = measureTimeMillis {
            rectRender.onDrawFrame(gl)
        }

        val t3 = measureTimeMillis {
            blitTexture()
        }

//        frameCounter.compute(t1, t2, t3)

    }

    private fun blitTexture() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0)
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fboId)

        glReadBuffer(GL_COLOR_ATTACHMENT0)
        glBlitFramebuffer(
            0, 0, sw, sh,
            0, 0, sw, sh,
            GL_COLOR_BUFFER_BIT, GL_LINEAR
        )
    }
}