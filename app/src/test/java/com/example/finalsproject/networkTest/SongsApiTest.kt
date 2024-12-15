package com.example.finalsproject.networkTest

import com.example.finalsproject.network.SongsApi
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
class SongsApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: SongsApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(SongsApi::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

     @Test
     fun songsApiTest_getRandomSongs() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\":200,\"msg\":\"Success\",\"size\":5,\"data\":[]}")
        mockWebServer.enqueue(mockResponse)

        val response = api.getRandomSongs(5, "Bearer token")
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
        Assert.assertEquals(responseBody?.size, 5)
     }

    @Test
    fun songsApiTest_getTopSongs() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\":200,\"msg\":\"Success\",\"size\":5,\"data\":[]}")
        mockWebServer.enqueue(mockResponse)

        val response = api.getTopSongs(5, "Bearer token")
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
        Assert.assertEquals(responseBody?.size, 5)
    }

    @Test
    fun songsApiTest_getSongSearchResult() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\":200,\"msg\":\"Success\",\"currentPage\":1,\"totalPage\":3,\"records\":8,\"data\":[]}")
        mockWebServer.enqueue(mockResponse)

        val response = api.getSongSearchResult(
            title = "title",
            artist = "artist",
            page = 1,
            size = 3,
            auth = "Bearer token"
        )
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
        Assert.assertEquals(responseBody?.totalPage, 3)
        Assert.assertEquals(responseBody?.records, 8)
    }

    @Test
    fun songsApiTest_getSongByEmotion() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\":200,\"msg\":\"Success\",\"size\":3,\"data\":[]}")
        mockWebServer.enqueue(mockResponse)

        val response = api.getSongByEmotion(
            message = "I am happy",
            size = 3,
            auth = "Bearer token"
        )
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
        Assert.assertEquals(responseBody?.size, 3)
    }

    @Test
    fun songsApiTest_getSongById() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\":200,\"msg\":\"Success\",\"data\":{\"id\": 3, \"title\": \"title\", \"artist\": \"artist\", \"album\": \"album\", \"albumImgBase64\": \"albumImgBase64\", \"genre\": \"genre\", \"length\": 120, \"releaseDate\": \"12/12/2000\", \"playCount\": 344, \"likeCount\": 12, \"likedByUser\": true}}")
        mockWebServer.enqueue(mockResponse)

        val response = api.getSongById(
            id = 3L,
            auth = "Bearer token"
        )
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
        Assert.assertEquals(responseBody?.data?.id, 3L)
    }

    @Test
    fun songsApiTest_getSongBlob() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        val response = api.getSongBlob(
            id = 3L
        )
        val responseBody = response.body()

        Assert.assertNotNull(responseBody)
    }
}