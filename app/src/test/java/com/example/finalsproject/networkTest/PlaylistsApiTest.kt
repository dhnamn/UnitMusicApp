package com.example.finalsproject.networkTest

import com.example.finalsproject.network.PlaylistsApi
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
class PlaylistsApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: PlaylistsApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(PlaylistsApi::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun playlistApiTest_getRandomSongs() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\":200,\"msg\":\"Success\",\"size\":5,\"data\":[]}")
        mockWebServer.enqueue(mockResponse)

        val response = api.getRandomPlaylists(5, "Bearer token")
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
        Assert.assertEquals(responseBody?.size, 5)
    }

    @Test
    fun playlistApiTest_getFullPlaylistById() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\":200,\"msg\":\"Success\",\"data\":null}")
        mockWebServer.enqueue(mockResponse)

        val response = api.getFullPlaylistById(5, "Bearer token")
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
    }

    @Test
    fun playlistApiTest_getPlaylistSearchResult() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\":200,\"msg\":\"Success\",\"currentPage\":1, \"totalPage\":3, \"records\":10, \"data\":[]}")
        mockWebServer.enqueue(mockResponse)

        val response = api.getPlaylistSearchResult("title",5, 1, "Bearer token")
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
    }
}