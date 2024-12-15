package com.example.finalsproject.viewModelTest

import android.annotation.SuppressLint
import com.example.finalsproject.data.LikedSongsNotifierRepo
import com.example.finalsproject.data.PlaylistsRepo
import com.example.finalsproject.data.SongsRepo
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.Song
import com.example.finalsproject.model.SongLikeEvent
import com.example.finalsproject.model.apiResponse.PlaylistsResponse
import com.example.finalsproject.model.apiResponse.SongsResponse
import com.example.finalsproject.ui.viewmodel.HomeScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
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
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class HomeScreenViewModelTest {
    @get: Rule
    val mockitoTestRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var songsRepo: SongsRepo

    @Mock
    private lateinit var playlistsRepo: PlaylistsRepo

    @Mock
    private lateinit var likeNotifier: LikedSongsNotifierRepo

    private lateinit var viewModel: HomeScreenViewModel

    private var mockLikeEvent = MutableSharedFlow<SongLikeEvent>()

    private val dispatcher = UnconfinedTestDispatcher()

    @SuppressLint("CheckResult")
    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        `when`(likeNotifier.likeEvent).thenReturn(mockLikeEvent)
        viewModel = HomeScreenViewModel(songsRepo, playlistsRepo, likeNotifier)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @SuppressLint("CheckResult")
    @Test
    fun homeScreenViewModelTest_onClickRetryGetTopSongs(): Unit = runBlocking {
        val listSongs = listOf(
            Song(1, "Song1", "Artist1", "album1", "", "genre1", 120, "01/01/2020", 53, 43, true),
            Song(2, "Song2", "Artist2", "album2", "", "genre2", 140, "02/02/2020", 43, 17, true),
            Song(3, "Song3", "Artist3", "album3", "", "genre3", 130, "03/03/2020", 33, 6, false),
            Song(4, "Song4", "Artist4", "album4", "", "genre4", 110, "04/04/2020", 33, 14, false),
            Song(5, "Song5", "Artist5", "album5", "", "genre5", 170, "05/05/2020", 13, 3, true)
        )

        val response = SongsResponse.DataList(200, "Success", 5, listSongs)

        doAnswer {
            val onResponse = it.getArgument<(SongsResponse.DataList) -> Unit>(1)
            onResponse(response)
        }.`when`(songsRepo).getTopSongs(any(), any(), any())

        viewModel.onClickRetryGetTopSongs()
//        Mockito.timeout(1000L)
//        Assert.assertTrue(viewModel.state.value.topSongs is FetchStatus.Ready)
    }

    @SuppressLint("CheckResult")
    @Test
    fun homeScreenViewModelTest_launchGetPlaylistsTask(): Unit = runBlocking {
        val response = PlaylistsResponse.DataList(200, "Success", 5, listOf())

        doAnswer {
            val onResponse = it.getArgument<(PlaylistsResponse.DataList) -> Unit>(1)
            onResponse(response)
        }.`when`(playlistsRepo).getRandomPlaylists(any(), any(), any())

        viewModel.onClickRetryGetPlaylists()
//        Mockito.timeout(1000)
//        Assert.assertTrue(viewModel.state.value.playlists is FetchStatus.Ready)
    }

    @SuppressLint("CheckResult")
    @Test
    fun homeScreenViewModelTest_launchGetRandomSongsTask(): Unit = runBlocking {
        val response = SongsResponse.DataList(200, "Success", 5, listOf())

        doAnswer {
            val onResponse = it.getArgument<(SongsResponse.DataList) -> Unit>(1)
            onResponse(response)
        }.`when`(songsRepo).getRandomSongs(any(), any(), any())

        viewModel.onClickRetryGetRandomSongs()
//        Mockito.timeout(1000)
//        Assert.assertTrue(viewModel.state.value.exploreSongs is FetchStatus.Ready)
    }
}
