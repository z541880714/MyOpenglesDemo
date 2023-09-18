package com.example.glesjavademo

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.viewinterop.AndroidView
import com.example.glesjavademo.render.*
import com.example.glesjavademo.render.fbo.FboMultiRenderTarget
import com.example.glesjavademo.render.fbo.FboMultiRenderTexture
import com.example.glesjavademo.render.fbo.FboParticle
import com.example.glesjavademo.render.transformfeedback.TransformFeedbackSample
import com.example.glesjavademo.ui.theme.GlesJavaDemoTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        it.setEGLContextClientVersion(2)
        //  it.setEGLConfigChooser(MyConfigChooser())

        it.setRenderer(createRenderer(8))
    }, modifier = Modifier.onSizeChanged {
        Log.i("log_zc", "MainActivity-> Greeting: size:$it")
    })
}


fun createRenderer(index: Int): GLSurfaceView.Renderer {
    return when (index) {
        0 -> TextureRender()
        1 -> VboParticleRender()
        2 -> VboTextureRender()
        3 -> AboParticleRender()
        4 -> FboMultiRenderTarget()
        5 -> FboMultiRenderTexture()
        6 -> FboParticle()
        7 -> LinesRender()
        8 -> UniformBlockRender()
        9 -> RectRender()
        10 -> TransformFeedbackSample()
        else -> VboTextureRender()
    }
}
