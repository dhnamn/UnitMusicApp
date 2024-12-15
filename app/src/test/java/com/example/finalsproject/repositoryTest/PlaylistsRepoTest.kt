package com.example.finalsproject.repositoryTest

import com.example.finalsproject.data.CredentialsRepo
import com.example.finalsproject.data.NetworkPlaylistsRepo
import com.example.finalsproject.model.Playlist
import com.example.finalsproject.model.apiResponse.PlaylistsResponse
import com.example.finalsproject.model.apiResponse.SongsResponse
import com.example.finalsproject.network.PlaylistsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import retrofit2.Response

@ExperimentalCoroutinesApi
class PlaylistsRepoTest {
    private lateinit var repo: NetworkPlaylistsRepo
    private lateinit var api: PlaylistsApi
    private lateinit var credentialsRepo: CredentialsRepo

    @Before
    fun setup() {
        api = mock()
        credentialsRepo = mock()
        repo = NetworkPlaylistsRepo(
            api = api,
            credentialsRepo = credentialsRepo
        )
    }

    @Test
    fun playlistsRepoTest_getRandomPlaylists() = runBlocking {
        val response = PlaylistsResponse.DataList(code = 200, msg = "Success", size = 3, data = listOf())
        `when`(api.getRandomPlaylists(eq(3), any())).thenReturn(Response.success(response))

        repo.getRandomPlaylists(
            size = 3,
            onResponse = { Assert.assertEquals(it.code, 200)},
            onFailure = {}
        )
    }

    @Test
    fun playlistsRepoTest_getFullPlaylistById() = runBlocking {
        val response = PlaylistsResponse.Full(code = 200, msg = "Success", data = Playlist(1L, "", "", "", listOf()))
        `when`(api.getFullPlaylistById(eq(1L), any())).thenReturn(Response.success(response))

        repo.getFullPlaylistById(
            id = 1L,
            onResponse = {
                Assert.assertEquals(it.code, 200)
                Assert.assertEquals(it.data?.id, 1L)
            },
            onFailure = {}
        )
    }

    @Test
    fun playlistsRepoTest_getPlaylistSearchResult() = runBlocking {
        val response = PlaylistsResponse.Search(code = 200, msg = "Success", currentPage = 1, totalPage = 3, records = 5, data = listOf())
        `when`(api.getPlaylistSearchResult(any(), any(), any(), any())).thenReturn(Response.success(response))

        repo.getPlaylistSearchResult(
            title = "title",
            size = 2,
            page = 1,
            onResponse = {
                Assert.assertEquals(it.code, 200)
            },
            onFailure = {}
        )
    }
}