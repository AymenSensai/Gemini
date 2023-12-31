package com.aymen.gemini

import android.graphics.Bitmap

data class GeminiState(
    val messages : List<Message> = emptyList(),
    val text: String = "",
    val image: Bitmap? = null,
    val response: String = "",
    val isLoading: Boolean = false
)