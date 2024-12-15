package com.example.finalsproject.network

import com.example.finalsproject.model.apiResponse.ShazamResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ShazamApi {
    @POST("songs/detect")
    @Headers(
        "x-rapidapi-host: $HOST",
        "Content-Type: text/plain"
    )
    suspend fun recognize(
        @Body rawAudio: RequestBody,
        @Header("x-rapidapi-key") key: String,
    ): Response<ShazamResponse.Recognize>

    companion object {
        const val HOST = "shazam.p.rapidapi.com"
    }
}