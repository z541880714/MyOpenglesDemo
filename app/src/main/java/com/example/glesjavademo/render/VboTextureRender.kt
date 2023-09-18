package com.example.glesjavademo.render

import android.opengl.GLES30.*
import android.opengl.GLSurfaceView
import com.example.glesjavademo.appContext
import com.example.glesjavademo.util.toFloatBuffer
import com.example.glesjavademo.util.zglGenAndBindTexture
import com.example.glesjavademo.util.zglCreateProgramIdFromAssets
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class VboTextureRender : GLSurfaceView.Renderer {

    private val vertexData = floatArrayOf(
        -1f, -1f, 0f, 1f,    // x, y , u, v  顶点坐标 和纹理坐标 一起...
        -1f, 1f, 0f, 0f,
        1f, 1f, 1f, 0f,
        1f, -1f, 1f, 1f,
    )

    private val pos = floatArrayOf(
        -1f, -1f,
        -1f, 1f,
        1f, 1f,
        1f, -1f
    ).toFloatBuffer()

    private val tex = floatArrayOf(
        0f, 1f,
        0f, 0f,
        1f, 0f,
        1f, 1f
    ).toFloatBuffer()

    private val indexData = intArrayOf(0, 1, 2, 0, 2, 3)

    private var imageTexture = 0

    private val vboIds = IntArray(3)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 创建GL程序
        // Create GL program
        val programId =
            zglCreateProgramIdFromAssets(appContext, "shader/vbo.vert", "shader/vbo.frag").programId
        // 应用GL程序
        // Use the GL program
        glUseProgram(programId)

        // 创建VBO和IBO
        // Create VBO and IBO
        glGenBuffers(vboIds.size, vboIds, 0)


        // 将顶点和纹理数据载入VBO
        // Load vertex data into VBO
        /*   val vertexDataBuffer = ByteBuffer.allocateDirect(vertexData.size * 4)
               .order(ByteOrder.nativeOrder()).asFloatBuffer()
           vertexDataBuffer.put(vertexData)
           vertexDataBuffer.position(0)
           glBindBuffer(GL_ARRAY_BUFFER, vboIds[0])
           glBufferData(
               GL_ARRAY_BUFFER, vertexDataBuffer.capacity() * 4, vertexDataBuffer, GL_STATIC_DRAW
           )
           glEnableVertexAttribArray(0)
           glEnableVertexAttribArray(1)
           glVertexAttribPointer(0, 2, GL_FLOAT, false, 16, 0)
           glVertexAttribPointer(1, 2, GL_FLOAT, false, 16, 8)
           glBindBuffer(GL_ARRAY_BUFFER, 0)*/

        glBindBuffer(GL_ARRAY_BUFFER, vboIds[0])
        glBufferData(
            GL_ARRAY_BUFFER, pos.limit() * 4, pos, GL_STATIC_DRAW
        )
        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 8, 0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)


        glBindBuffer(GL_ARRAY_BUFFER, vboIds[1])
        glBufferData(
            GL_ARRAY_BUFFER, tex.limit() * 4, tex, GL_STATIC_DRAW
        )
        glEnableVertexAttribArray(1)
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 8, 0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)


        // 将顶点索引数据载入IBO
        // Load index data into IBO
        val indexDataBuffer = ByteBuffer.allocateDirect(indexData.size * 4)
            .order(ByteOrder.nativeOrder()).asIntBuffer()
        indexDataBuffer.put(indexData)
        indexDataBuffer.position(0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIds[2])
        glBufferData(
            GL_ELEMENT_ARRAY_BUFFER, indexDataBuffer.limit() * 4, indexDataBuffer, GL_STATIC_DRAW
        )

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    var glSurfaceViewWidth = 0
    var glSurfaceViewHeight = 0

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glSurfaceViewWidth = width
        glSurfaceViewHeight = height
        // 设置视口，这里设置为整个GLSurfaceView区域
        // Set the viewport to the full GLSurfaceView
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        // 设置清屏颜色
        // Set the color which the screen will be cleared to
        glClearColor(0.9f, 0.9f, 0.9f, 1f)

        // 清屏
        // Clear the screen
        glClear(GL_COLOR_BUFFER_BIT)
        if (imageTexture == 0)
            imageTexture = appContext.assets.open("images/image_2.jpg").use {
                zglGenAndBindTexture(it)
            }
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, imageTexture)

        // 设置好状态，准备渲染
        // Set the status before rendering
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIds[2])
        // 调用glDrawElements方法用TRIANGLES的方式执行渲染
        // Call the glDrawElements method with GL_TRIANGLES
        glDrawElements(GL_TRIANGLES, indexData.size, GL_UNSIGNED_INT, 0)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)

    }
}