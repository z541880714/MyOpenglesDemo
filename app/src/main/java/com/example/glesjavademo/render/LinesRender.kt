package com.example.glesjavademo.render

import android.graphics.PointF
import android.opengl.GLES10.GL_ONE
import android.opengl.GLES10.glBlendFunc
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.glesjavademo.appContext
import com.example.glesjavademo.util.FrameCounter
import com.example.glesjavademo.util.zglCreateProgramIdFromAssets
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random
import kotlin.system.measureTimeMillis

class LinesRender() : GLSurfaceView.Renderer {

    //两个点 画一条线...
    private val pointsCount = 2
    private val lineCount = 10000
    private var sw: Int = 0
    private var sh: Int = 0

    private val frameCounter = FrameCounter()

    private val pointsBuffer =
        ByteBuffer.allocate(lineCount * pointsCount * (2 * 4)).order(ByteOrder.nativeOrder())

    private val bioArrays = IntBuffer.allocate(1)


    private val aboBuffer = IntBuffer.allocate(1)

    private val aboId: Int get() = aboBuffer[0]
    private val vboId get() = bioArrays[0]

    private var programId = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val programData =
            zglCreateProgramIdFromAssets(appContext, "shader/line.vert", "shader/line.frag")
        Log.i("log_zc", "ParticleRender-> onSurfaceCreated: program: $programData")
        programId = programData.programId

        GLES30.glGenBuffers(bioArrays.limit(), bioArrays)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboId)
        GLES30.glBufferData(
            GLES30.GL_ARRAY_BUFFER,
            pointsBuffer.limit(),
            pointsBuffer,
            GLES30.GL_DYNAMIC_DRAW
        )
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)

        GLES30.glGenVertexArrays(1, aboBuffer)
        GLES30.glBindVertexArray(aboId)

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboId)

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);

        GLES30.glVertexAttribPointer(0, 2, GLES30.GL_FLOAT, false, 2 * 4, 0)
        GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 2 * 4, 2 * 4)

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)

        GLES30.glBindVertexArray(0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        this.sw = width
        this.sh = height
    }

    override fun onDrawFrame(gl: GL10?) {

        GLES30.glUseProgram(programId)

        val t1 = measureTimeMillis {
            updatePoints()
        }

        GLES30.glBindVertexArray(aboId)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboId)

        val t2 = measureTimeMillis {
            GLES30.glBufferSubData(GLES30.GL_ARRAY_BUFFER, 0, pointsBuffer.limit(), pointsBuffer)
        }

        val t3 = measureTimeMillis {
            GLES30.glDrawArrays(GLES30.GL_LINES, 0, lineCount)
        }

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
        GLES30.glBindVertexArray(0)

        frameCounter.compute(t1, t2, t3)
    }

    private var renderLines = Array(lineCount) { PointF(0f, 0f) }
    private fun updatePoints() {
        pointsBuffer.position(0)
        for (i in 0 until lineCount) {
            val from = renderLines[i]
            //random 比较 耗时. . 性能 优化时 需要注意...
            val deltaX = 0.03.toFloat()
            var deltaY = 0f

            var x = (from.x + deltaX)
            var y = 0f
            if (x >= 1f) {
                deltaY = (Random.nextFloat() * 2 - 1) * 0.1f
                x = -1f
                y = (from.y + deltaY).coerceAtLeast(-1f).coerceAtMost(1f)
                //避免 跨屏 渲染...
                from.x = x
                from.y = y
            } else {
                y = (from.y + deltaY).coerceAtLeast(-1f).coerceAtMost(1f)
            }

            pointsBuffer.putFloat(from.x)
            pointsBuffer.putFloat(from.y)
            pointsBuffer.putFloat(x)
            pointsBuffer.putFloat(y)

            from.x = x
            from.y = y
        }
        pointsBuffer.rewind()


    }
}