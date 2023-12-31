package com.aymen.gemini

sealed class GeminiEffect {
    data class ShowSnackBar(val message: String) : GeminiEffect()
}
