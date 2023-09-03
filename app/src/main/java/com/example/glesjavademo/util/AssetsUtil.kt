@file:JvmName("AssetsUtil")
package com.example.glesjavademo.util

import android.content.res.AssetManager

fun AssetManager.readAssetsFile(path: String): String {
    return open(path).bufferedReader().use {
        it.readText()
    }
}