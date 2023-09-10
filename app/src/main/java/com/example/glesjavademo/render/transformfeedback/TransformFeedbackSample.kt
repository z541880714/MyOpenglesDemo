package com.example.glesjavademo.render.transformfeedback

import android.graphics.PointF
import android.opengl.GLES20
import android.opengl.GLES20.GL_BLEND
import android.opengl.GLES20.GL_ONE
import android.opengl.GLES20.GL_SRC_ALPHA
import android.opengl.GLES20.glBlendFunc
import android.opengl.GLES20.glUniform2f
import android.opengl.GLES20.glUniform4f
import android.opengl.GLES30.GL_ARRAY_BUFFER
import android.opengl.GLES30.GL_COLOR_BUFFER_BIT
import android.opengl.GLES30.GL_DYNAMIC_COPY
import android.opengl.GLES30.GL_FLOAT
import android.opengl.GLES30.GL_INTERLEAVED_ATTRIBS
import android.opengl.GLES30.GL_POINTS
import android.opengl.GLES30.GL_RASTERIZER_DISCARD
import android.opengl.GLES30.GL_SYNC_GPU_COMMANDS_COMPLETE
import android.opengl.GLES30.GL_TEXTURE0
import android.opengl.GLES30.GL_TEXTURE_2D
import android.opengl.GLES30.GL_TIMEOUT_IGNORED
import android.opengl.GLES30.GL_TRANSFORM_FEEDBACK_BUFFER
import android.opengl.GLES30.glActiveTexture
import android.opengl.GLES30.glBeginTransformFeedback
import android.opengl.GLES30.glBindBuffer
import android.opengl.GLES30.glBindBufferBase
import android.opengl.GLES30.glBindTexture
import android.opengl.GLES30.glBufferData
import android.opengl.GLES30.glClear
import android.opengl.GLES30.glDeleteSync
import android.opengl.GLES30.glDisable
import android.opengl.GLES30.glDrawArrays
import android.opengl.GLES30.glEnable
import android.opengl.GLES30.glEnableVertexAttribArray
import android.opengl.GLES30.glEndTransformFeedback
import android.opengl.GLES30.glFenceSync
import android.opengl.GLES30.glGenBuffers
import android.opengl.GLES30.glGetUniformLocation
import android.opengl.GLES30.glLinkProgram
import android.opengl.GLES30.glTransformFeedbackVaryings
import android.opengl.GLES30.glUniform1f
import android.opengl.GLES30.glUniform1i
import android.opengl.GLES30.glUseProgram
import android.opengl.GLES30.glViewport
import android.opengl.GLES30.glWaitSync
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.glesjavademo.appContext
import com.example.glesjavademo.util.FrameCounter
import com.example.glesjavademo.util.toFloatBuffer
import com.example.glesjavademo.util.zglCreateProgramFromCodeStr
import com.example.glesjavademo.util.zglCreateProgramIdFromAssets
import com.example.glesjavademo.util.zglGenAndBindTexture
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random


private const val NUM_PARTICLES = 200000

class TransformFeedbackSample : GLSurfaceView.Renderer {

    private var sWidth = 0
    private var sHeight = 0
    private val frameCounter = FrameCounter()


    private val vertexDataBuffer =
        FloatArray(Particle.stride / 4 * NUM_PARTICLES).toFloatBuffer().apply {
            repeat(NUM_PARTICLES) {
                this.put(Particle.floatArray())
            }
            position(0)
        }

    //emit vert shader 中的 feedback 参数.
    private val feedbackVaryingArray = arrayOf(
        "v_position",
        "v_velocity",
        "v_size",
        "v_curtime",
        "v_lifetime"
    )

    private var emitProgramId = 0
    private var emitTimeLoc = 0

    //用来同步 多个program 之间的同步..
    private var emitSync = 0L

    private var drawProgramId = 0

    private var time = 0f
    private var startTime = 0L

    private val vboIds = IntArray(2)


    private fun initEmitParticles() {
        emitProgramId = zglCreateProgramIdFromAssets(
            appContext,
            "shader/particle/particleEmit.vert",
            "shader/particle/particleEmit.frag",
        ).programId
        glTransformFeedbackVaryings(emitProgramId, feedbackVaryingArray, GL_INTERLEAVED_ATTRIBS)
        glLinkProgram(emitProgramId)

        emitTimeLoc = glGetUniformLocation(emitProgramId, "u_time")
    }

    private fun init() {
        initEmitParticles()

        drawProgramId = zglCreateProgramIdFromAssets(
            appContext,
            "shader/particle/particleDraw.vert",
            "shader/particle/particleDraw.frag"
        ).programId

        glGenBuffers(2, vboIds, 0)
        repeat(2) {
            glBindBuffer(GL_ARRAY_BUFFER, vboIds[it])
            glBufferData(
                GL_ARRAY_BUFFER,
                vertexDataBuffer.limit() * 4,
                vertexDataBuffer,
                GL_DYNAMIC_COPY
            )
        }
    }


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        init()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        sWidth = width
        sHeight = height
    }

    override fun onDrawFrame(gl: GL10?) {
        val cur = System.currentTimeMillis()
        if (time == 0f) {
            startTime = cur
        }
        val deltaTime = (cur - startTime).coerceAtLeast(1).toFloat()
        time = deltaTime
        // 将 一个 缓冲区A 数据作为输入, 经过计算, 输出到 另一个 缓冲区B..
        // 下一帧, 将缓冲区B 作为输入, 计算后将结果输出到 缓冲区A
        // 以此 进行 轮循..
        emitParticles(deltaTime)
        //将 emitProgram 输出的缓冲区 作为输入, 进行渲染..
        draw()

        frameCounter.compute()
    }


    private fun draw() {
        // 等待之前的命令 执行结束.
        glWaitSync(emitSync, 0, GL_TIMEOUT_IGNORED)
        // 删除同步.. 下次再创建..
        glDeleteSync(emitSync)

        glViewport(0, 0, sWidth, sHeight)
        glClear(GL_COLOR_BUFFER_BIT)
        glUseProgram(drawProgramId)
        //绑定顶点 属性数据.
        //此时 frameId 指向的是 emitProgram 输出的 顶点缓冲区
        setupVertexAttributes(vboIds[frameIndex])

        // Blend particles
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        glDrawArrays(GL_POINTS, 0, NUM_PARTICLES);
    }


    private fun setupVertexAttributes(vboId: Int) {
        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        GLES20.glVertexAttribPointer(0, 2, GL_FLOAT, false, Particle.stride, 0)
        GLES20.glVertexAttribPointer(1, 2, GL_FLOAT, false, Particle.stride, 2 * 4)
        GLES20.glVertexAttribPointer(2, 1, GL_FLOAT, false, Particle.stride, 4 * 4)
        GLES20.glVertexAttribPointer(3, 1, GL_FLOAT, false, Particle.stride, 5 * 4)
        GLES20.glVertexAttribPointer(4, 1, GL_FLOAT, false, Particle.stride, 6 * 4)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
        glEnableVertexAttribArray(2)
        glEnableVertexAttribArray(3)
        glEnableVertexAttribArray(4)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    private var frameIndex = 0
    private fun emitParticles(deltaTime: Float) {
        val srcVboId = vboIds[frameIndex]   //顶点 缓冲区
        val dstVboId = vboIds[(frameIndex + 1) % 2]  // feedback 缓冲区

        // emitProgramId 不需要 渲染
        glUseProgram(emitProgramId)
        setupVertexAttributes(srcVboId)

        glBindBuffer(GL_TRANSFORM_FEEDBACK_BUFFER, dstVboId)
        glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, 0, dstVboId)

        // Turn off rasterization - we are not drawing
        //使用GL_RASTERIZER_DISCARD标志作为参数调用glEnable()函数，
        // 告诉渲染管线在transform feedback可选阶段之后和到达光栅器前抛弃所有的图元。
        // 后面记得需要在合适的位置 调用 glDisable(GL_RASTERIZER_DISCARD)
        glEnable(GL_RASTERIZER_DISCARD)

        glUniform1f(emitTimeLoc, time)

        glBeginTransformFeedback(GL_POINTS)
        glDrawArrays(GL_POINTS, 0, NUM_PARTICLES)
        glEndTransformFeedback()

        //后续 使用  glWaitSync,  glDeleteSync 函数进行同步
        emitSync = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0)

        glDisable(GL_RASTERIZER_DISCARD)
        glUseProgram(0) // emitProgramId 结束
        glBindBuffer(GL_TRANSFORM_FEEDBACK_BUFFER, 0)

        frameIndex = (frameIndex + 1) % 2  // 下一帧 两个 帧缓冲区 进行输出 输入 交换.
    }

}

private data class Particle(
    val position: PointF = PointF(0f, 0f),
    val velocity: PointF = PointF(0f, 0f),
    var size: Float = 0f,
    var curtime: Float = 0f,
    var lifetime: Float = 0f
) {
    companion object {
        val stride get() = 28
        fun floatArray() =
            FloatArray(stride / 4).apply {
                this[2] = (Random.nextFloat() * 2 - 1) * 0.1f //x 方向的 随机速度  -1 到1
                this[3] = Random.nextFloat() * 0.3f
            }
    }

}