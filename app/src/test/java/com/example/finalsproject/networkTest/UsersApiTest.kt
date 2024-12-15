package com.example.finalsproject.networkTest

import com.example.finalsproject.model.apiRequest.UserPlaylistRequest
import com.example.finalsproject.network.UsersApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@ExperimentalCoroutinesApi
class UsersApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: UsersApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(UsersApi::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun usersApiTest_getUserInfo() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\": 200, \"msg\": \"Success\", \"data\": {\"id\": 1,\"username\": \"username\",\"email\": \"email\",\"avatarImgBase64\": \"img\",\"createDate\": \"01/01/2000\"}}")
        mockWebServer.enqueue(mockResponse)

        val response = api.getUserInfo("Bearer token")
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
        Assert.assertEquals(responseBody?.data?.id, 1L)
    }

//    @Test
//    fun usersApiTest_uploadUserAvatar() = runBlocking {
//        val mockResponse = MockResponse()
//            .setResponseCode(200)
//            .setBody("{\"code\": 200, \"msg\": \"Success\", \"data\": {\"id\": 1,\"username\": \"username\",\"email\": \"email\",\"avatarImgBase64\": \"value\",\"createDate\": \"01/01/2000\"}}")
//        mockWebServer.enqueue(mockResponse)
//
//        val file = MultipartBody.Part.createFormData("file", "value")
//        val response = api.uploadUserAvatar(file,"Bearer token")
//        val responseBody = response.body()
//
//        Assert.assertEquals(responseBody?.code, 200)
//        Assert.assertEquals(responseBody?.data?.avatarImgBase64, "value")
//    }

    @Test
    fun usersApiTest_likeSong() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\": 200, \"msg\": \"Success\", \"data\": \"User like song\"}")
        mockWebServer.enqueue(mockResponse)

        val response = api.likeSong(1,"Bearer token")
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
    }

    @Test
    fun usersApiTest_unlikeSong() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\": 200, \"msg\": \"Success\", \"data\": \"User unlike song\"}")
        mockWebServer.enqueue(mockResponse)

        val response = api.unlikeSong(1,"Bearer token")
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
    }

    @Test
    fun usersApiTest_getLikedSongs() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\":200,\"msg\":\"Success\",\"currentPage\":1,\"totalPage\":3,\"records\":8,\"data\":[]}")
        mockWebServer.enqueue(mockResponse)

        val response = api.getLikedSongs(1, 1,"Bearer token")
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
    }

    @Test
    fun usersApiTest_createPlaylist() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\": 200, \"msg\": \"Success\"}")
        mockWebServer.enqueue(mockResponse)

        val response = api.createPlaylist(UserPlaylistRequest.Create("title", "description"),"Bearer token")
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
    }

    @Test
    fun usersApiTest_getAllUserPlaylists() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\": 200, \"msg\": \"Success\", \"size\": 3, \"data\": []}")
        mockWebServer.enqueue(mockResponse)

        val response = api.getAllUserPlaylists("Bearer token")
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
    }

    @Test
    fun usersApiTest_getUserPlaylist() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\": 200, \"msg\": \"Success\", \"data\": {\"id\": 1,\"title\": \"title\",\"description\": \"description\",\"songs\": []}}")
        mockWebServer.enqueue(mockResponse)

        val response = api.getUserPlaylist(1,"Bearer token")
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
        Assert.assertEquals(responseBody?.data?.id, 1L)
    }

    @Test
    fun usersApiTest_updateUserPlaylist() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\": 200, \"msg\": \"Success\"}")
        mockWebServer.enqueue(mockResponse)

        val response = api.updateUserPlaylist(1, UserPlaylistRequest.Update("new_title", "description"),"Bearer token")
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
    }

    @Test
    fun usersApiTest_deleteUserPlaylist() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\": 200, \"msg\": \"Success\"}")
        mockWebServer.enqueue(mockResponse)

        val response = api.deleteUserPlaylist(1,"Bearer token")
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
    }

    @Test
    fun usersApiTest_addSongToUserPlaylist() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\": 200, \"msg\": \"Success add song 1 to playlist 2\"}")
        mockWebServer.enqueue(mockResponse)

        val response = api.addSongToUserPlaylist(1, 2,"Bearer token")
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
        Assert.assertEquals(responseBody?.msg, "Success add song 1 to playlist 2")
    }

    @Test
    fun usersApiTest_removeSongFromUserPlaylist() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"code\": 200, \"msg\": \"Success remove song 1 from playlist 2\"}")
        mockWebServer.enqueue(mockResponse)

        val response = api.removeSongFromUserPlaylist(1, 2,"Bearer token")
        val responseBody = response.body()

        Assert.assertEquals(responseBody?.code, 200)
        Assert.assertEquals(responseBody?.msg, "Success remove song 1 from playlist 2")
    }
}

