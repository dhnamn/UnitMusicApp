package com.example.finalsproject.networkTest

import com.example.finalsproject.network.ShazamApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@ExperimentalCoroutinesApi
class ShazamApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: ShazamApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ShazamApi::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun shazamApiTest_recognize() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"track\": {\"title\": \"title\",\"subtitle\": \"artist\"\"images\": null,\"hub\": null}}")
        mockWebServer.enqueue(mockResponse)

        val response = api.recognize(ByteArray(0).toRequestBody(null, 0, 0), "Bearer token")
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.track?.title, "title")
    }
}