package com.aymen.gemini

import android.graphics.Bitmap

sealed class GeminiEvent {

    data class OnTextChange(val text: String) : GeminiEvent()
    data class OnImageChange(val imageBitmap: Bitmap) : GeminiEvent()
    data object OnGetImage : GeminiEvent()

}