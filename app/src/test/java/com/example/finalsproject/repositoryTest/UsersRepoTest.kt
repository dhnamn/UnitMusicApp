package com.example.finalsproject.repositoryTest

import com.example.finalsproject.data.CredentialsRepo
import com.example.finalsproject.data.NetworkUsersRepo
import com.example.finalsproject.model.User
import com.example.finalsproject.model.UserPlaylist
import com.example.finalsproject.model.apiRequest.UserPlaylistRequest
import com.example.finalsproject.model.apiResponse.SongsResponse
import com.example.finalsproject.model.apiResponse.UserPlaylistResponse
import com.example.finalsproject.model.apiResponse.UserResponse
import com.example.finalsproject.network.UsersApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import retrofit2.Response
import retrofit2.http.Multipart

@ExperimentalCoroutinesApi
class UsersRepoTest {
    private lateinit var repo: NetworkUsersRepo
    private lateinit var api: UsersApi
    private lateinit var credentialsRepo: CredentialsRepo

    @Before
    fun setup() {
        api = mock()
        credentialsRepo = mock()
        repo = NetworkUsersRepo(
            api = api,
            credentialsRepo = credentialsRepo
        )
    }

    @Test
    fun usersRepoTest_getUserInfo() = runBlocking {
        val response = UserResponse.Info(code = 200, msg = "Success", data = User(1L, "username", "email@gmail.com", "avatar", "01/01/2024"))
        `when`(api.getUserInfo(any())).thenReturn(Response.success(response))

        repo.getUserInfo(
            onResponse = {
                Assert.assertEquals(it.code, 200)
                Assert.assertEquals(it.data?.id, 1L)
            },
            onFailure = {}
        )
    }

//    @Test
//    fun usersRepoTest_uploadUserAvatar() = runBlocking {
//        val response = UserResponse.Info(code = 200, msg = "Success", data = User(1L, "username", "email@gmail.com", "new_avatar", "01/01/2024"))
//        `when`(api.uploadUserAvatar(any(),any())).thenReturn(Response.success(response))
//
//        repo.uploadUserAvatar(
//            file = ,
//            onResponse = {
//                Assert.assertEquals(it.code, 200)
//                Assert.assertEquals(it.data?.avatarImgBase64, "new_avatar")
//            },
//            onFailure = {}
//        )
//    }

    @Test
    fun usersRepoTest_likeSong() = runBlocking {
        val response = UserResponse.Message(code = 200, msg = "Success", data = "User like song!")
        `when`(api.likeSong(eq(1), any())).thenReturn(Response.success(response))

        repo.likeSong(
            id = 1L,
            onResponse = {
                Assert.assertEquals(it.code, 200)
                Assert.assertEquals(it.data, "User like song!")
            },
            onFailure = {}
        )
    }

    @Test
    fun usersRepoTest_unlikeSong() = runBlocking {
        val response = UserResponse.Message(code = 200, msg = "Success", data = "User unlike song!")
        `when`(api.unlikeSong(eq(1), any())).thenReturn(Response.success(response))

        repo.unlikeSong(
            id = 1L,
            onResponse = {
                Assert.assertEquals(it.code, 200)
                Assert.assertEquals(it.data, "User unlike song!")
            },
            onFailure = {}
        )
    }

    @Test
    fun usersRepoTest_getLikedSongs() = runBlocking {
        val response = UserResponse.SongList(code = 200, msg = "Success", currentPage = 1, totalPage = 3, records = 10, data = listOf())
        `when`(api.getLikedSongs(eq(3), eq(1), any())).thenReturn(Response.success(response))

        repo.getLikedSongs(
            size = 3,
            page = 1,
            onResponse = {
                Assert.assertEquals(it.code, 200)
            },
            onFailure = {}
        )
    }

    @Test
    fun usersRepoTest_createPlaylist() = runBlocking {
        val response = UserPlaylistResponse.Create(code = 200, msg = "Success")
        `when`(api.createPlaylist(any(), any())).thenReturn(Response.success(response))

        repo.createPlaylist(
            body = UserPlaylistRequest.Create("title", "description"),
            onResponse = {
                Assert.assertEquals(it.code, 200)
            },
            onFailure = {}
        )
    }

    @Test
    fun usersRepoTest_deleteUserPlaylist() = runBlocking {
        val response = UserPlaylistResponse.Delete(code = 200, msg = "Success delete playlist 1")
        `when`(api.deleteUserPlaylist(eq(1), any())).thenReturn(Response.success(response))

        repo.deleteUserPlaylist(
            playlistId = 1L,
            onResponse = {
                Assert.assertEquals(it.code, 200)
                Assert.assertEquals(it.msg, "Success delete playlist 1")
            },
            onFailure = {}
        )
    }

    @Test
    fun usersRepoTest_getAllUserPlaylist() = runBlocking {
        val response = UserPlaylistResponse.DataList(code = 200, msg = "Success", size = 1, data = listOf())
        `when`(api.getAllUserPlaylists(any())).thenReturn(Response.success(response))

        repo.getAllUserPlaylist(
            onResponse = {
                Assert.assertEquals(it.code, 200)
                Assert.assertEquals(it.size, 1)
            },
            onFailure = {}
        )
    }

    @Test
    fun usersRepoTest_updateUserPlaylist() = runBlocking {
        val response = UserPlaylistResponse.Update(code = 200, msg = "Success")
        `when`(api.updateUserPlaylist(eq(1), any(), any())).thenReturn(Response.success(response))

        repo.updateUserPlaylist(
            playlistId = 1L,
            body = UserPlaylistRequest.Update("new_title", "new_desc"),
            onResponse = {
                Assert.assertEquals(it.code, 200)
            },
            onFailure = {}
        )
    }

    @Test
    fun usersRepoTest_getUserPlaylist() = runBlocking {
        val response = UserPlaylistResponse.Full(code = 200, msg = "Success", data = UserPlaylist(1L, "title", "description", listOf()))
        `when`(api.getUserPlaylist(eq(1), any())).thenReturn(Response.success(response))

        repo.getUserPlaylist(
            playlistId = 1L,
            onResponse = {
                Assert.assertEquals(it.code, 200)
                Assert.assertEquals(it.data?.id, 1L)
            },
            onFailure = {}
        )
    }

    @Test
    fun usersRepoTest_addSongToUserPlaylist() = runBlocking {
        val response = UserPlaylistResponse.Add(code = 200, msg = "Success")
        `when`(api.addSongToUserPlaylist(eq(1), eq(2), any())).thenReturn(Response.success(response))

        repo.addSongToUserPlaylist(
            songId = 1L,
            playlistId = 2L,
            onResponse = {
                Assert.assertEquals(it.code, 200)
            },
            onFailure = {}
        )
    }

    @Test
    fun usersRepoTest_removeSongFromUserPlaylist() = runBlocking {
        val response = UserPlaylistResponse.Remove(code = 200, msg = "Success")
        `when`(api.removeSongFromUserPlaylist(eq(1), eq(2), any())).thenReturn(Response.success(response))

        repo.removeSongFromUserPlaylist(
            songId = 1L,
            playlistId = 2L,
            onResponse = {
                Assert.assertEquals(it.code, 200)
            },
            onFailure = {}
        )
    }
}