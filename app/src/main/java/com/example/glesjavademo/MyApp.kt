@file:JvmName("AppContext")

package com.example.glesjavademo

import android.app.Application
import android.content.Context


lateinit var appContext: Context
    private set

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}