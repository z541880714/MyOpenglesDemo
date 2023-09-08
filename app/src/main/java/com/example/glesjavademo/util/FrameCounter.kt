package com.example.glesjavademo.util

import android.os.SystemClock
import android.util.Log

class FrameCounter {
    private var index = 0L
    private var lastTimestamp = 0L

    var framePerSecond = 0
        private set

    fun compute(vararg time: Long, callback: (Int) -> Unit = {}) {
        index++
        if (lastTimestamp == 0L) {
            lastTimestamp = SystemClock.elapsedRealtime()
            return
        }
        if (index % 60 == 0L) {
            val cur = SystemClock.elapsedRealtime()
            framePerSecond = (60f * 1000 / (cur - lastTimestamp)).toInt()
            lastTimestamp = cur
            callback(framePerSecond)
            Log.i(
                "log_zc",
                "ParticleRender-> frameCounter: frame:${framePerSecond},  times:${time.toList()}"
            )
        }
    }
}