package com.example.finalsproject.network

import com.example.finalsproject.model.apiRequest.GeminiRequest
import com.example.finalsproject.model.apiResponse.GeminiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

//const val geminiKey = "AIzaSyAL4xOZMqUtd_Ea0Unjad2ACriLWzQ98no"

interface GeminiApi {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun getStory(
        @Query("key") key: String,
        @Body body: GeminiRequest,
        @Header("Content-Type") contentType: String = "application/json"
    ) : Response<GeminiResponse>
}