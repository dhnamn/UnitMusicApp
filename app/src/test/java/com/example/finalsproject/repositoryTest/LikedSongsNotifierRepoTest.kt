package com.example.finalsproject.repositoryTest

import com.example.finalsproject.data.CredentialsRepo
import com.example.finalsproject.data.NetworkLikedSongsNotifierRepo
import com.example.finalsproject.data.NetworkSongsRepo
import com.example.finalsproject.data.NetworkUsersRepo
import com.example.finalsproject.data.UsersRepo
import com.example.finalsproject.model.Song
import com.example.finalsproject.model.SongLikeEvent
import com.example.finalsproject.model.apiResponse.SongsResponse
import com.example.finalsproject.model.apiResponse.UserResponse
import com.example.finalsproject.network.SongsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import retrofit2.Response

@ExperimentalCoroutinesApi
class LikedSongsNotifierRepoTest {
    private lateinit var repo: NetworkLikedSongsNotifierRepo
    private lateinit var userRepo: UsersRepo

    @Before
    fun setup() {
        userRepo = mock()
        repo = NetworkLikedSongsNotifierRepo(userRepo)
    }

    @Test
    fun likedSongsNotifierRepoTest_likeSong() = runBlocking {
        val response = UserResponse.Message(code = 200, msg = "Success", data = "User like song")
        `when`(userRepo.likeSong(eq(1L), any(), any())).doAnswer {
            val onResponse = it.getArgument<(UserResponse.Message) -> Unit>(1)
            onResponse(response)
        }

        repo.likeSong(id = 1L)

        val event = repo.likeEvent.first()
        Assert.assertEquals(SongLikeEvent(1L, true), event)
    }

    @Test
    fun likedSongsNotifierRepoTest_unlikeSong() = runBlocking {
        val response = UserResponse.Message(code = 200, msg = "Success", data = "User unlike song")
        `when`(userRepo.unlikeSong(eq(1L), any(), any())).doAnswer {
            val onResponse = it.getArgument<(UserResponse.Message) -> Unit>(1)
            onResponse(response)
        }

        repo.unlikeSong(id = 1L)

        val event = repo.likeEvent.first()
        Assert.assertEquals(SongLikeEvent(1L, false), event)
    }

    @Test
    fun likedSongsNotifierRepoTest_toggleLikeSong() = runBlocking {
        val song = Song(1L, "", "", "", "", "", 122, "", 111,3, true)

        repo.toggleLike(song)

        Mockito.verify(userRepo).unlikeSong(
            eq(1L),
            any(),
            any()
        )
    }
}