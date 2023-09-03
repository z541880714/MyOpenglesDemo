package com.example.glesjavademo.render

import android.opengl.GLES20.*
import android.opengl.GLES30.*
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.glesjavademo.appContext
import com.example.glesjavademo.util.glCreateProgramIdFromAssets
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random

class ParticleRender : GLSurfaceView.Renderer {

    val N = 1000;
    private var sw: Int = 0
    private var sh: Int = 0
    val pointsBuffer =
        ByteBuffer.allocateDirect(N * 6 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()

    private val buffers = IntArray(2)
    private val vboId get() = buffers[0]
    private val uboId get() = buffers[1]


    private val floatArray: FloatArray = floatArrayOf(
        0.8f, 0.1f,
        1.0f, 1.0f, 1.0f, 1.0f,
        0.8f, 0.2f,
        1.0f, 0.0f, 0.0f, 1.0f,
        0.8f, 0.3f,
        0.0f, 0.0f, 1.0f, 1.0f,
        0.8f, 0.4f,
        0.3f, 0.3f, 1.0f, 1.0f
    )

    private val vertices: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(floatArray.size * 4).order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply {
                put(floatArray)
                rewind()
            }
    }


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        val programData =
            glCreateProgramIdFromAssets(appContext, "shader/points.vert", "shader/points.frag")
        Log.i("log_zc", "ParticleRender-> onSurfaceCreated: program: $programData")
        val programId = programData.programId
        glUseProgram(programId)

        glGenBuffers(buffers.size, buffers, 0)

        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, floatArray.size * 4, null, GL_DYNAMIC_DRAW)
        glBindBuffer(GL_ARRAY_BUFFER, 0)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        this.sw = width
        this.sh = height

    }

    override fun onDrawFrame(gl: GL10?) {

        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferSubData(GL_ARRAY_BUFFER, 0, floatArray.size * 4, vertices)

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, (2 + 4) * 4, 0);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, (2 + 4) * 4, 2 * 4);


        glDrawArrays(GL_POINTS, 0, 4)

        glBindBuffer(GL_ARRAY_BUFFER, 0)

    }


    private fun updatePoints() {
        pointsBuffer.position(0)
        repeat(N) {
            val x = Random.nextDouble().toFloat()
            val y = Random.nextDouble().toFloat()
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