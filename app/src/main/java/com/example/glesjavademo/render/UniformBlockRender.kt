package com.example.glesjavademo.render

import android.opengl.GLES20
import android.opengl.GLES30.*
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.glesjavademo.appContext
import com.example.glesjavademo.util.toFloatBuffer
import com.example.glesjavademo.util.zglCreateProgramIdFromAssets
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random

/**
 * 使用 gles 3.0 特性 变换反馈 渲染粒子系统
 */
class UniformBlockRender : GLSurfaceView.Renderer {

    private val vaoIds = IntArray(1)
    private val vboIds = IntArray(2)
    private val uboIds = IntArray(1)
    private var sWidth = 0
    private var sHeight = 0

    // 渲染 点的尺寸..
    private val pointSize = 20f

    //渲染 点的 数量.
    private val pointCount = 10
    private val uniformBlockName get() = "UniformBlock"

    private val vertexCoord =
        FloatArray(pointCount * 2) { Random.nextFloat() * 1000 }.toFloatBuffer()

    /**
     * uniform block  数据.
     */
    private val uniformBlockData = FloatArray(5) { 0f }.toFloatBuffer()

    private var programId = 0
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        val programData = zglCreateProgramIdFromAssets(
            appContext,
            "shader/uniformBlock.vert",
            "shader/uniformBlock.frag",
        )
        programId = programData.programId
        initUbo()
        initVaoVertex()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        this.sWidth = width
        this.sHeight = height
        updateUboData()
    }


    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_SRC_ALPHA)
        glUseProgram(programId)

        glBindVertexArray(vaoIds[0])               //bind vao
        glBindBuffer(GL_ARRAY_BUFFER, vboIds[0])  // bind vbo
        glDrawArrays(GL_POINTS, 0, pointCount)
        glBindBuffer(GL_ARRAY_BUFFER, 0) // unbind vbo
        glBindVertexArray(0)  // unbind vao
    }


    /**
     * 使用  vao vbo 初始化 顶点属性..
     */
    private fun initVaoVertex() {
        glGenVertexArrays(1, vaoIds, 0)
        GLES20.glGenBuffers(2, vboIds, 0)

        glBindVertexArray(vaoIds[0])
        glBindBuffer(GL_ARRAY_BUFFER, vboIds[0])
        glBufferData(GL_ARRAY_BUFFER, vertexCoord.limit() * 4, vertexCoord, GL_STATIC_DRAW)
        glEnableVertexAttribArray(0)
        GLES20.glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * 4, 0)


        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
    }


    private fun initUbo() {
        glGenBuffers(1, uboIds, 0)
        glBindBuffer(GL_UNIFORM_BUFFER, uboIds[0])
        glBufferData(GL_UNIFORM_BUFFER, uniformBlockData.limit() * 4, null, GL_STATIC_DRAW)
        glBindBuffer(GL_UNIFORM_BUFFER, 0)
        //把 uniform 缓冲对象绑定到同样的绑定点上,
        glBindBufferRange(GL_UNIFORM_BUFFER, 0, uboIds[0], 0, uniformBlockData.limit() * 4)


        val uniformLocation = glGetUniformBlockIndex(programId, uniformBlockName)
        //绑定 uniform 位置.. 后续 使用 ubo  绑定数据..
        glUniformBlockBinding(programId, uniformLocation, 0)

    }

    private fun updateUboData() {
        uniformBlockData.put(sWidth.toFloat()).put(sHeight.toFloat())
            .put(4, pointSize)
            .position(0)

        glBindBuffer(GL_UNIFORM_BUFFER, uboIds[0])
        glBufferSubData(GL_UNIFORM_BUFFER, 0, uniformBlockData.limit() * 4, uniformBlockData)
        glBindBuffer(GL_UNIFORM_BUFFER, 0)
    }


}