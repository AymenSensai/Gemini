package com.aymen.gemini

import android.graphics.Bitmap

data class Message(
    val text: String,
    val image: Bitmap?,
    val role: String
)
