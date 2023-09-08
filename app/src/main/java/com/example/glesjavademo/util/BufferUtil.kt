@file:JvmName("BufferUtil")

package com.example.glesjavademo.util

import java.nio.ByteBuffer
import java.nio.ByteOrder


fun FloatArray.toFloatBuffer() =
    ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        .put(this).apply { position(0) }


fun IntArray.toIntBuffer() =
    ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder()).asIntBuffer()
        .put(this).apply { position(0) }

fun ShortArray.toShortBuffer() =
    ByteBuffer.allocateDirect(size * 2).order(ByteOrder.nativeOrder()).asShortBuffer()
        .put(this).apply { position(0) }

fun ByteArray.toByteBuffer() =
    ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder())
        .put(this).apply { position(0) }