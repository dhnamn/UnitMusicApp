package com.example.finalsproject.model.apiResponse

import com.example.finalsproject.model.apiRequest.GeminiContent
import kotlinx.serialization.Serializable


@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>,
    val usageMetadata: UsageMetadata,
    val modelVersion: String
) {
    @Serializable
    data class Candidate(
        val content: Content,
        val finishReason: String,
        val avgLogprobs: Double
    ) {
        @Serializable
        data class Content(
            val parts: List<GeminiContent.Part>,
            val role: String
        )
    }
    @Serializable
    data class UsageMetadata(
        val promptTokenCount: Int,
        val candidatesTokenCount: Int,
        val totalTokenCount: Int
    )
}
