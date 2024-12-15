package com.example.finalsproject.repositoryTest

import com.example.finalsproject.data.CredentialsRepo
import com.example.finalsproject.data.NetworkSongsRepo
import com.example.finalsproject.model.Song
import com.example.finalsproject.model.apiResponse.SongsResponse
import com.example.finalsproject.network.SongsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import retrofit2.Response

@ExperimentalCoroutinesApi
class SongsRepoTest {
    private lateinit var repo: NetworkSongsRepo
    private lateinit var api: SongsApi
    private lateinit var credentialsRepo: CredentialsRepo

    @Before
    fun setup() {
        api = mock()
        credentialsRepo = mock()
        repo = NetworkSongsRepo(
            baseUrl = "/",
            api = api,
            credentialsRepo = credentialsRepo
        )
    }

    @Test
    fun songsRepoTest_getRandomSongs() = runBlocking {
        val response = SongsResponse.DataList(code = 200, msg = "Success", data = listOf())
        `when`(api.getRandomSongs(any(), any())).thenReturn(Response.success(response))

        repo.getRandomSongs(
            size = 5,
            onResponse = { Assert.assertEquals(it.code, 200)},
            onFailure = {}
        )
    }

    @Test
    fun songsRepoTest_getTopSongs() = runBlocking {
        val response = SongsResponse.DataList(code = 200, msg = "Success", data = listOf())
        `when`(api.getTopSongs(any(), any())).thenReturn(Response.success(response))

        repo.getRandomSongs(
            size = 5,
            onResponse = { Assert.assertEquals(it.code, 200)},
            onFailure = {}
        )
    }

    @Test
    fun songsRepoTest_getSongSearchResult() = runBlocking {
        val response = SongsResponse.SongSearch(code = 200, msg = "Success", currentPage = 1, totalPage = 2, records = 5, data = listOf())
        `when`(api.getSongSearchResult(eq("title"),eq("artist"),eq(1),eq(3), any())).thenReturn(Response.success(response))

        repo.getSongSearchResult(
            size = 3,
            title = "title",
            artist = "artist",
            page = 1,
            onResponse = { Assert.assertEquals(it.code, 200) },
            onFailure = {},
        )
    }

    @Test
    fun songsRepoTest_getSongByEmotion() = runBlocking {
        val response = SongsResponse.SongSearchByEmotion(code = 200, msg = "Success", size = 1, data = listOf())
        `when`(api.getSongByEmotion(eq("message"),eq(3), any())).thenReturn(Response.success(response))

        repo.getSongByEmotion(
            message = "message",
            size = 3,
            onResponse = { Assert.assertEquals(it.code, 200) },
            onFailure = {},
        )
    }

    @Test
    fun songsRepoTest_getSongById() = runBlocking {
        val response = SongsResponse.GetSingle(code = 200, msg = "Success", data = Song(id = 1, title = "title", artist = "artist", album = "album", albumImgBase64 = "img", genre = "", length = 120, releaseDate = "", playCount = 2, likeCount = 1, likedByUser = true))
        `when`(api.getSongById(eq(1), any())).thenReturn(Response.success(response))

        repo.getSongById(
            id = 1L,
            onResponse = {
                Assert.assertEquals(it.code, 200)
                Assert.assertEquals(it.data?.id, 1L)
            },
            onFailure = {},
        )
    }
}