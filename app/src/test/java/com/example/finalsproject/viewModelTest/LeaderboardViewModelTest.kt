package com.example.finalsproject.viewModelTest

import android.annotation.SuppressLint
import androidx.compose.ui.text.input.TextFieldValue
import com.example.finalsproject.data.AuthRepo
import com.example.finalsproject.data.CredentialsRepo
import com.example.finalsproject.data.LikedSongsNotifierRepo
import com.example.finalsproject.data.SongsRepo
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.Song
import com.example.finalsproject.model.SongLikeEvent
import com.example.finalsproject.model.apiResponse.SongsResponse
import com.example.finalsproject.ui.viewmodel.LeaderboardViewModel
import com.example.finalsproject.ui.viewmodel.LoginScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class LeaderboardViewModelTest {
    @get: Rule
    val mockitoTestRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var songsRepo: SongsRepo

    @Mock
    private lateinit var likedSongsNotifierRepo: LikedSongsNotifierRepo

    private lateinit var viewModel: LeaderboardViewModel

    private val dispatcher = UnconfinedTestDispatcher()
    private var mockLikeEvent = MutableSharedFlow<SongLikeEvent>()

    @SuppressLint("CheckResult")
    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        `when`(likedSongsNotifierRepo.likeEvent).thenReturn(mockLikeEvent)
        viewModel = LeaderboardViewModel(songsRepo, likedSongsNotifierRepo)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun leaderboardViewModelTest_onClickRetryGetTopSongs(): Unit = runBlocking {
        val songs = listOf(Song(1L, "", "", "", "", "", 123, "", 133, 12, true))
        val response = SongsResponse.DataList(200, "Success", 20, data = songs)

        `when`(songsRepo.getTopSongs(eq(20), any(), any())).doAnswer {
            val onResponse = it.getArgument<(SongsResponse.DataList) -> Unit>(1)
            onResponse(response)
        }

        viewModel.onClickRetryGetTopSongs()

        Assert.assertEquals(viewModel.state.value.topSongs, FetchStatus.Ready(songs))
    }
}