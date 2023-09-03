package com.example.glesjavademo.render

import android.opengl.GLES30.*
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.glesjavademo.appContext
import com.example.glesjavademo.util.glCreateProgramIdFromAssets
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random

class VboParticleRender : GLSurfaceView.Renderer {

    private val pointsCount = 10000;
    private var sw: Int = 0
    private var sh: Int = 0
    val pointsBuffer =
        ByteBuffer.allocateDirect(pointsCount * 6 * 4).order(ByteOrder.nativeOrder())
            .asFloatBuffer()

    private val buffers = IntArray(1)
    private val vboId get() = buffers[0]

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        val programData =
            glCreateProgramIdFromAssets(appContext, "shader/points.vert", "shader/points.frag")
        Log.i("log_zc", "ParticleRender-> onSurfaceCreated: program: $programData")
        val programId = programData.programId
        glUseProgram(programId)

        glGenBuffers(buffers.size, buffers, 0)

        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, pointsBuffer.limit() * 4, null, GL_DYNAMIC_DRAW)
        glBindBuffer(GL_ARRAY_BUFFER, 0)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        this.sw = width
        this.sh = height

    }


    override fun onDrawFrame(gl: GL10?) {
//        glClearColor(0f, 0f, 0f, 1f)
        glClear(GL_COLOR_BUFFER_BIT)
        glEnable(GL_BLEND);
        frameCounter()

        updatePoints()


        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferSubData(GL_ARRAY_BUFFER, 0, pointsBuffer.limit() * 4, pointsBuffer)

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, (2 + 4) * 4, 0);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, (2 + 4) * 4, 2 * 4);

        glDrawArrays(GL_POINTS, 0, pointsCount)
        glBindBuffer(GL_ARRAY_BUFFER, 0)

    }


    private var index = 0L
    private var lastTimestamp = 0L
    private fun frameCounter() {
        index++
        if (lastTimestamp == 0L) {
            lastTimestamp = System.currentTimeMillis()
            return
        }
        if (index % 60 == 0L) {

            val cur = System.currentTimeMillis()
            val frame = (60f * 1000 / (cur - lastTimestamp)).toInt()
            lastTimestamp = cur
            Log.i("log_zc", "ParticleRender-> frameCounter: frame:${frame}")
        }
    }


    private fun updatePoints() {
        pointsBuffer.position(0)
        repeat(pointsCount) {
            val x = Random.nextDouble().toFloat() * 2 - 1
            val y = Random.nextDouble().toFloat() * 2 - 1
            val r = y
            val g = 80 / 255f
            val b = 80 / 255f
            val a = 1f
            pointsBuffer.put(x)
            pointsBuffer.put(y)
            pointsBuffer.put(r)
            pointsBuffer.put(g)
            pointsBuffer.put(b)
            pointsBuffer.put(a)
        }
        pointsBuffer.rewind()
    }

}