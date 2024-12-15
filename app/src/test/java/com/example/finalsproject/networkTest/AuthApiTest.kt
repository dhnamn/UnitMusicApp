package com.example.finalsproject.networkTest

import com.example.finalsproject.model.apiRequest.AuthRequest
import com.example.finalsproject.network.AuthApi
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
class AuthApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: AuthApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(AuthApi::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun authApiTest_postRegister() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\":200,\"msg\":\"Success\"}")
        mockWebServer.enqueue(mockResponse)

        val response = api.postRegister(AuthRequest.Register("username", "email", "password"))
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
    }

    @Test
    fun authApiTest_postConfirmation() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\":200,\"msg\":\"Success\"}")
        mockWebServer.enqueue(mockResponse)

        val response = api.postConfirmation(AuthRequest.Confirmation("username", "111111"))
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
    }

    @Test
    fun authApiTest_postLogin() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\":200,\"msg\":\"Success\",\"token\":\"token\"}")
        mockWebServer.enqueue(mockResponse)

        val response = api.postLogin(AuthRequest.Login("username", "password"))
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
        Assert.assertEquals(responseBody?.token, "token")
    }
}