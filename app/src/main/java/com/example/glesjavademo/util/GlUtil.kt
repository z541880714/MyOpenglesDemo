@file:JvmName("GLUtil")

package com.example.glesjavademo.util

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES30.*
import java.io.InputStream
import java.nio.ByteBuffer


data class ShaderProgramData(val programId: Int, val vShaderId: Int, val fShaderId: Int)

fun zglCreateProgramIdFromAssets(
    context: Context, vertPath: String, fragPath: String
): ShaderProgramData {
    val vertCode = context.assets.readAssetsFile(vertPath)
    val fragCode = context.assets.readAssetsFile(fragPath)
    val programId = glCreateProgram()
    val vertShader = zglLoadShader(GL_VERTEX_SHADER, vertCode)
    val fragShader = zglLoadShader(GL_FRAGMENT_SHADER, fragCode)

    glAttachShader(programId, vertShader)
    glAttachShader(programId, fragShader)
    glLinkProgram(programId)
    return ShaderProgramData(programId, vertShader, fragShader)
}

fun zglLoadShader(shaderType: Int, code: String): Int {
    val shaderId = glCreateShader(shaderType)
    glShaderSource(shaderId, code)
    glCompileShader(shaderId)
    return shaderId
}


fun zglGenTexture(bitmapStream: InputStream): Int {
    val textures = IntArray(1)
    glGenTextures(1, textures, 0)

    val bitmap = BitmapFactory.decodeStream(bitmapStream)
    val buffer = ByteBuffer.allocate(bitmap.width * bitmap.height * 4)
    bitmap.copyPixelsToBuffer(buffer)
    bitmap.recycle()
    buffer.position(0)
    zglBindTexture(textures[0], buffer, bitmap.width, bitmap.height)
    return textures[0]
}

fun zglBindTexture(textureId: Int, buffer: ByteBuffer?, width: Int, height: Int) {
    glBindTexture(GL_TEXTURE_2D, textureId)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
    glTexImage2D(
        GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA,
        GL_UNSIGNED_BYTE, buffer
    )
}

