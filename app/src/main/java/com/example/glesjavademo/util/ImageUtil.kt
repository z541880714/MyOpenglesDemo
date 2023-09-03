package com.example.glesjavademo.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

//将image 格式转换成 RGB8888 格式...
fun Context.decodeBitmapFromAssets(path: String): Bitmap {
    val bitmap = assets.open(path).use {
        BitmapFactory.decodeStream(it)
    }
    return bitmap.copy(Bitmap.Config.ARGB_8888, false)
}