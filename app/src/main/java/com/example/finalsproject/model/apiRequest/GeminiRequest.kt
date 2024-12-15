package com.example.finalsproject.model.apiRequest

import kotlinx.serialization.Serializable

@Serializable
data class GeminiContent(
    val parts: List<Part>
) {
    @Serializable
    data class Part(
        val text: String
    )
}

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>
) {
    companion object {
        fun make(prompt: String) = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiContent.Part(text = prompt)
                    )
                )
            )
        )
    }
}