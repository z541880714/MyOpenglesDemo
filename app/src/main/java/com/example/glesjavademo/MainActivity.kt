package com.example.glesjavademo

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.glesjavademo.config.MyConfigChooser
import com.example.glesjavademo.render.VboParticleRender
import com.example.glesjavademo.ui.theme.GlesJavaDemoTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GlesJavaDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    AndroidView(factory = { GLSurfaceView(it) }, update = {
        it.setEGLContextClientVersion(3)
        //设置 EGL
        //  it.setEGLConfigChooser(MyConfigChooser())
        val renderer = VboParticleRender()
        it.setRenderer(renderer)
    })
}
