package com.example.glesjavademo.render

import android.opengl.GLES30.*
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.glesjavademo.appContext
import com.example.glesjavademo.util.zglCreateProgramIdFromAssets
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random
import kotlin.system.measureTimeMillis

/**
 * 使用 abo 方式渲染..
 */
class AboParticleRender : GLSurfaceView.Renderer {

    private val pointsCount = 100;
    private val pointsPerLine = 20
    private val lineCount = pointsCount / pointsPerLine
    private var sw: Int = 0
    private var sh: Int = 0
    val pointsBuffer =
        ByteBuffer.allocateDirect(pointsCount * 6 * 4).order(ByteOrder.nativeOrder())

    private val buffers = IntArray(1)
    private val vboId get() = buffers[0]

    private val aboBuffer = IntBuffer.allocate(1)
    private val aboId: Int get() = aboBuffer[0]

    private var programId = 0
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        val programData =
            zglCreateProgramIdFromAssets(appContext, "shader/points.vert", "shader/points.frag")
        Log.i("log_zc", "ParticleRender-> onSurfaceCreated: program: $programData")
        programId = programData.programId

        glGenBuffers(buffers.size, buffers, 0)

        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, pointsBuffer.limit(), null, GL_DYNAMIC_DRAW)
        glBindBuffer(GL_ARRAY_BUFFER, 0)

        glGenVertexArrays(1, aboBuffer)
        glBindVertexArray(aboId)

        glBindBuffer(GL_ARRAY_BUFFER, vboId)

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * 4 + 4, 0)
        glVertexAttribPointer(1, 4, GL_UNSIGNED_BYTE, true, 2 * 4 + 4, 2 * 4)

        glBindBuffer(GL_ARRAY_BUFFER, 0)

        glBindVertexArray(0)


    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        this.sw = width
        this.sh = height

    }


    override fun onDrawFrame(gl: GL10?) {

        glUseProgram(programId)

        val t1 = measureTimeMillis {
            updatePoints()
        }


        val t2 = measureTimeMillis {
            glBindVertexArray(aboId)
            glBindBuffer(GL_ARRAY_BUFFER, vboId)
            glBufferSubData(GL_ARRAY_BUFFER, 0, pointsBuffer.limit(), pointsBuffer)

            repeat(lineCount) {
                glDrawArrays(GL_POINTS, it * pointsPerLine, pointsPerLine)
            }

            glBindBuffer(GL_ARRAY_BUFFER, 0)
            glBindVertexArray(0)
        }

        frameCounter(t1, t2)

    }


    private var index = 0L
    private var lastTimestamp = 0L
    private fun frameCounter(t1: Long, t2: Long) {
        index++
        if (lastTimestamp == 0L) {
            lastTimestamp = System.currentTimeMillis()
            return
        }
        if (index % 60 == 0L) {

            val cur = System.currentTimeMillis()
            val frame = (60f * 1000 / (cur - lastTimestamp)).toInt()
            lastTimestamp = cur
            Log.i("log_zc", "ParticleRender-> frameCounter: frame:${frame}, t1:$t1 , t2:$t2")
        }
    }

    private fun updatePoints() {
        pointsBuffer.position(0)

        for (i in 0 until lineCount) {
            for (j in 0 until pointsPerLine) {
                //random 比较 耗时. . 性能 优化时 需要注意...
                val x = Random.nextDouble().toFloat() * 2 - 1
                val y = Random.nextDouble().toFloat() * 2 - 1


                val r = ((y + 1) / 2 * 255).toInt().toByte()
                val g = ((x + 1) / 2 * 255).toInt().toByte()
                val b = (1f * 255).toInt().toByte()
                val a = (1f * 255).toInt().toByte()
                pointsBuffer.putFloat(x)
                pointsBuffer.putFloat(y)
                pointsBuffer.put(r)
                pointsBuffer.put(g)
                pointsBuffer.put(b)
                pointsBuffer.put(a)
            }
        }

        pointsBuffer.rewind()
    }

}