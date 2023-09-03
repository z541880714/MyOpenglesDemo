package com.example.glesjavademo.config

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLDisplay

class MyConfigChooser : GLSurfaceView.EGLConfigChooser {
    override fun chooseConfig(egl: EGL10, display: EGLDisplay): EGLConfig? {
        val configs = arrayOfNulls<EGLConfig>(1)
        val attribs = intArrayOf(
            EGL10.EGL_RENDERABLE_TYPE, 4,  // EGL_OPENGL_ES2_BIT
            EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
            EGL10.EGL_RED_SIZE, 8,
            EGL10.EGL_GREEN_SIZE, 8,
            EGL10.EGL_BLUE_SIZE, 8,
            EGL10.EGL_DEPTH_SIZE, 16,
            EGL10.EGL_SAMPLE_BUFFERS, GLES20.GL_TRUE,
            EGL10.EGL_SAMPLES, 4,  // 在这里修改MSAA的倍数，4就是4xMSAA，再往上开程序可能会崩
            EGL10.EGL_NONE
        )

        egl.eglChooseConfig(display, attribs, configs, 1, IntArray(1))
        Log.i("log_zc", "MyConfigChooser-> chooseConfig: is null?:${configs[0] == null}")
        return configs[0]
    }
}