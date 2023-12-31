package com.aymen.gemini

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class GeminiViewModel : ViewModel() {

    private val visionGenerativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-pro-vision",
            apiKey = BuildConfig.apiKey
        )
    }

    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.apiKey
        )
    }

    var state by mutableStateOf(GeminiState())
        private set

    var effect = MutableSharedFlow<GeminiEffect>()
        private set

    fun onEvent(event: GeminiEvent) {
        when (event) {
            is GeminiEvent.OnTextChange -> onTextChange(event.text)
            is GeminiEvent.OnImageChange -> onImageChange(event.imageBitmap)
            GeminiEvent.OnGetImage -> onGetImage()
        }
    }

    private fun onTextChange(text: String) {
        state = state.copy(text = text)
    }

    private fun onImageChange(imageBitmap: Bitmap) {
        state = state.copy(image = imageBitmap)
    }

    private fun onGetImage() {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val text = state.text
                val image = state.image
                val userMessage = Message(text = text, image = image, role = "user")

                state = state.copy(
                    isLoading = true,
                    messages = listOf(userMessage) + state.messages,
                    text = "",
                    image = null,
                )

                val response = if (image != null) {
                    visionGenerativeModel.generateContent(content {
                        image(image)
                        text(text)

                    })
                } else {
                    generativeModel.generateContent(text)
                }

                val geminiMessage =
                    Message(text = response.text.toString(), image = null, role = "gemini")

                state = state.copy(
                    messages = listOf(geminiMessage) + state.messages,
                    isLoading = false
                )

            } catch (e: Exception) {
                effect.emit(
                    GeminiEffect.ShowSnackBar(
                        message = e.message ?: "There was an error"
                    )
                )
                state = state.copy(isLoading = false)
            }

        }
    }
}