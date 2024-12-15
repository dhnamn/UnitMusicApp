package com.example.finalsproject.networkTest

import com.example.finalsproject.model.apiRequest.GeminiRequest
import com.example.finalsproject.network.GeminiApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@ExperimentalCoroutinesApi
class GeminiApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: GeminiApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(GeminiApi::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun geminiApiTest_getStory() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"candidates\": [],\"usageMetadata\": {\"promptTokenCount\": 1,\"candidatesTokenCount\": 1,\"totalTokenCount\": 20 },\"modelVersion\": \"gemini\"}")
        mockWebServer.enqueue(mockResponse)

        val response = api.getStory("gemini_key", GeminiRequest(listOf()), "application/json")
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.modelVersion, "gemini")
    }
}