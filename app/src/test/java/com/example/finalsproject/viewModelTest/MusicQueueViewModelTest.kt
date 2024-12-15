package com.example.finalsproject.viewModelTest

import android.annotation.SuppressLint
import com.example.finalsproject.data.GeminiRepo
import com.example.finalsproject.data.LikedSongsNotifierRepo
import com.example.finalsproject.data.PlaylistsRepo
import com.example.finalsproject.data.SongsRepo
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.Song
import com.example.finalsproject.model.SongLikeEvent
import com.example.finalsproject.model.apiResponse.SongsResponse
import com.example.finalsproject.ui.viewmodel.HomeScreenViewModel
import com.example.finalsproject.ui.viewmodel.MusicQueueState
import com.example.finalsproject.ui.viewmodel.MusicQueueViewModel
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
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer

@ExperimentalCoroutinesApi
class MusicQueueViewModelTest {
    @get: Rule
    val mockitoTestRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var songsRepo: SongsRepo

    @Mock
    private lateinit var geminiRepo: GeminiRepo

    @Mock
    private lateinit var likeNotifier: LikedSongsNotifierRepo

    private lateinit var viewModel: MusicQueueViewModel

    private var mockLikeEvent = MutableSharedFlow<SongLikeEvent>()

    private val dispatcher = UnconfinedTestDispatcher()

    @SuppressLint("CheckResult")
    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        `when`(likeNotifier.likeEvent).thenReturn(mockLikeEvent)
        viewModel = MusicQueueViewModel(songsRepo, geminiRepo, likeNotifier)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun musicQueueViewModelTest_goToPrev(): Unit = runBlocking {
        val listSongs = listOf(
            Song(1, "Song1", "Artist1", "album1", "", "genre1", 120, "01/01/2020", 53, 43, true),
            Song(2, "Song2", "Artist2", "album2", "", "genre2", 140, "02/02/2020", 43, 17, true),
            Song(3, "Song3", "Artist3", "album3", "", "genre3", 130, "03/03/2020", 33, 6, false),
            Song(4, "Song4", "Artist4", "album4", "", "genre4", 110, "04/04/2020", 33, 14, false),
            Song(5, "Song5", "Artist5", "album5", "", "genre5", 170, "05/05/2020", 13, 3, true)
        )

        viewModel.setQueue(listSongs)
        viewModel.goToPrev()
        Assert.assertEquals(viewModel.state.value.currentIdx, listSongs.size - 1)
    }

    @Test
    fun musicQueueViewModelTest_goToNextExplicitly(): Unit = runBlocking {
        val listSongs = listOf(
            Song(1, "Song1", "Artist1", "album1", "", "genre1", 120, "01/01/2020", 53, 43, true),
            Song(2, "Song2", "Artist2", "album2", "", "genre2", 140, "02/02/2020", 43, 17, true),
            Song(3, "Song3", "Artist3", "album3", "", "genre3", 130, "03/03/2020", 33, 6, false),
            Song(4, "Song4", "Artist4", "album4", "", "genre4", 110, "04/04/2020", 33, 14, false),
            Song(5, "Song5", "Artist5", "album5", "", "genre5", 170, "05/05/2020", 13, 3, true)
        )

        viewModel.setQueue(listSongs)
        viewModel.goToNextExplicitly()
        Assert.assertEquals(viewModel.state.value.currentIdx, 1)
    }

    @Test
    fun musicQueueViewModelTest_jumpTo(): Unit = runBlocking {
        val listSongs = listOf(
            Song(1, "Song1", "Artist1", "album1", "", "genre1", 120, "01/01/2020", 53, 43, true),
            Song(2, "Song2", "Artist2", "album2", "", "genre2", 140, "02/02/2020", 43, 17, true),
            Song(3, "Song3", "Artist3", "album3", "", "genre3", 130, "03/03/2020", 33, 6, false),
            Song(4, "Song4", "Artist4", "album4", "", "genre4", 110, "04/04/2020", 33, 14, false),
            Song(5, "Song5", "Artist5", "album5", "", "genre5", 170, "05/05/2020", 13, 3, true)
        )

        val target = Song(4, "Song4", "Artist4", "album4", "", "genre4", 110, "04/04/2020", 33, 14, false)

        viewModel.setQueue(listSongs)
        viewModel.jumpTo(target)
        Assert.assertEquals(viewModel.state.value.currentIdx, 3)
    }

    @Test
    fun musicQueueViewModelTest_add(): Unit = runBlocking {
        val listSongs = listOf(
            Song(1, "Song1", "Artist1", "album1", "", "genre1", 120, "01/01/2020", 53, 43, true),
            Song(2, "Song2", "Artist2", "album2", "", "genre2", 140, "02/02/2020", 43, 17, true),
            Song(3, "Song3", "Artist3", "album3", "", "genre3", 130, "03/03/2020", 33, 6, false),
            Song(4, "Song4", "Artist4", "album4", "", "genre4", 110, "04/04/2020", 33, 14, false)
        )

        val songAdd = Song(5, "Song5", "Artist5", "album5", "", "genre4", 110, "04/04/2020", 33, 14, false)

        viewModel.setQueue(listSongs)
        viewModel.add(songAdd)
        Assert.assertEquals(viewModel.state.value.queue.contains(songAdd), true)
    }

    @Test
    fun musicQueueViewModelTest_setQueue(): Unit = runBlocking {
        val listSongs = listOf(
            Song(1, "Song1", "Artist1", "album1", "", "genre1", 120, "01/01/2020", 53, 43, true),
            Song(2, "Song2", "Artist2", "album2", "", "genre2", 140, "02/02/2020", 43, 17, true),
            Song(3, "Song3", "Artist3", "album3", "", "genre3", 130, "03/03/2020", 33, 6, false),
            Song(4, "Song4", "Artist4", "album4", "", "genre4", 110, "04/04/2020", 33, 14, false)
        )

        viewModel.setQueue(listSongs)
        Assert.assertEquals(viewModel.state.value.queue, listSongs)
    }

    @Test
    fun musicQueueViewModelTest_remove(): Unit = runBlocking {
        val listSongs = listOf(
            Song(1, "Song1", "Artist1", "album1", "", "genre1", 120, "01/01/2020", 53, 43, true),
            Song(2, "Song2", "Artist2", "album2", "", "genre2", 140, "02/02/2020", 43, 17, true),
            Song(3, "Song3", "Artist3", "album3", "", "genre3", 130, "03/03/2020", 33, 6, false),
            Song(4, "Song4", "Artist4", "album4", "", "genre4", 110, "04/04/2020", 33, 14, false),
            Song(5, "Song5", "Artist5", "album5", "", "genre5", 170, "05/05/2020", 13, 3, true)
        )

        val removeSong = Song(4, "Song4", "Artist4", "album4", "", "genre4", 110, "04/04/2020", 33, 14, false)

        viewModel.setQueue(listSongs)
        viewModel.remove(removeSong)
        Assert.assertEquals(viewModel.state.value.queue.contains(removeSong), false)
    }

    @Test
    fun musicQueueViewModelTest_clear(): Unit = runBlocking {
        val listSongs = listOf(
            Song(1, "Song1", "Artist1", "album1", "", "genre1", 120, "01/01/2020", 53, 43, true),
            Song(2, "Song2", "Artist2", "album2", "", "genre2", 140, "02/02/2020", 43, 17, true),
            Song(3, "Song3", "Artist3", "album3", "", "genre3", 130, "03/03/2020", 33, 6, false),
            Song(4, "Song4", "Artist4", "album4", "", "genre4", 110, "04/04/2020", 33, 14, false),
            Song(5, "Song5", "Artist5", "album5", "", "genre5", 170, "05/05/2020", 13, 3, true)
        )

        viewModel.setQueue(listSongs)
        viewModel.clear()
        Assert.assertEquals(viewModel.state.value.queue.size, 0)
    }

    @Test
    fun musicQueueViewModelTest_togglePlayOrPause(): Unit = runBlocking {
        val listSongs = listOf(
            Song(1, "Song1", "Artist1", "album1", "", "genre1", 120, "01/01/2020", 53, 43, true),
            Song(2, "Song2", "Artist2", "album2", "", "genre2", 140, "02/02/2020", 43, 17, true),
            Song(3, "Song3", "Artist3", "album3", "", "genre3", 130, "03/03/2020", 33, 6, false),
            Song(4, "Song4", "Artist4", "album4", "", "genre4", 110, "04/04/2020", 33, 14, false),
            Song(5, "Song5", "Artist5", "album5", "", "genre5", 170, "05/05/2020", 13, 3, true)
        )
        viewModel.setQueue(listSongs)
        viewModel.togglePlayOrPause()

        Assert.assertEquals(viewModel.state.value.queue, listSongs)
    }

    @Test
    fun musicQueueViewModelTest_toggleShuffleMode(): Unit = runBlocking {
        viewModel.toggleShuffleMode()
        Assert.assertEquals(viewModel.state.value.shuffleMode, true)
        viewModel.toggleShuffleMode()
        Assert.assertEquals(viewModel.state.value.shuffleMode, false)
    }

    @Test
    fun musicQueueViewModelTest_cycleRepeatMode(): Unit = runBlocking {
        viewModel.cycleRepeatMode()
        Assert.assertEquals(viewModel.state.value.repeatMode, MusicQueueState.RepeatMode.ALL)
        viewModel.cycleRepeatMode()
        Assert.assertEquals(viewModel.state.value.repeatMode, MusicQueueState.RepeatMode.ONE)
    }

    @Test
    fun musicQueueViewModelTest_seek(): Unit = runBlocking {
        val listSongs = listOf(
            Song(1, "Song1", "Artist1", "album1", "", "genre1", 120, "01/01/2020", 53, 43, true),
            Song(2, "Song2", "Artist2", "album2", "", "genre2", 140, "02/02/2020", 43, 17, true),
        )
        viewModel.setQueue(listSongs)
        viewModel.seek(12000f)

        Assert.assertEquals(viewModel.progressStateMs.value, 12000f)
    }
}