package com.example.finalsproject.data

import com.example.finalsproject.BuildConfig
import com.example.finalsproject.model.apiRequest.GeminiRequest
import com.example.finalsproject.network.GeminiApi


interface GeminiRepo {
    suspend fun getStory(
        body: GeminiRequest
    ) : Result<String?>
}

class NetworkGeminiRepo(
    private val geminiApi: GeminiApi
) : GeminiRepo {
    override suspend fun getStory(
        body: GeminiRequest
    ) : Result<String?> {
        return try {
            val response = geminiApi.getStory(
                key = BuildConfig.GEMINI_API_KEY,
                body = body
            )
            if (response.isSuccessful) {
                Result.success(response.body()?.candidates?.get(0)?.content?.parts?.get(0)?.text)
            } else {
                Result.failure(RuntimeException("Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}