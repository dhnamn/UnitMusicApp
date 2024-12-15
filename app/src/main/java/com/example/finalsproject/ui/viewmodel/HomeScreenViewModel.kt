package com.example.finalsproject.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.finalsproject.App
import com.example.finalsproject.data.LikedSongsNotifierRepo
import com.example.finalsproject.data.PlaylistsRepo
import com.example.finalsproject.data.SongsRepo
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.Playlist
import com.example.finalsproject.model.Song
import com.example.finalsproject.model.apiResponse.PlaylistsResponse
import com.example.finalsproject.model.apiResponse.SongsResponse
import com.example.finalsproject.utils.updateLike
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "HomeScreenViewModel"

data class HomeScreenState(
    val topSongs: FetchStatus<List<Song>> = FetchStatus.Idle,
    val playlists: FetchStatus<List<Playlist>> = FetchStatus.Idle,
    val exploreSongs: FetchStatus<List<Song>> = FetchStatus.Idle,
)

class HomeScreenViewModel(
    private val songsRepo: SongsRepo,
    private val playlistsRepo: PlaylistsRepo,
    val likeNotifier: LikedSongsNotifierRepo,
) : ViewModel() {
    private val mutState = MutableStateFlow(HomeScreenState())
    val state = mutState.asStateFlow()

    init {
        viewModelScope.launch {
            likeNotifier.likeEvent.collect { event ->
                mutState.value.run {
                    if (topSongs is FetchStatus.Ready) {
                        val updated = topSongs.data.updateLike(event)
                        mutState.update { it.copy(topSongs = FetchStatus.Ready(updated)) }
                    }
                    if (exploreSongs is FetchStatus.Ready) {
                        val updated = exploreSongs.data.updateLike(event)
                        mutState.update { it.copy(exploreSongs = FetchStatus.Ready(updated)) }
                    }
                }
            }
        }
        launchGetTopSongsTask()
        launchGetPlaylistsTask()
        launchGetRandomSongsTask()
    }

    fun onClickRetryGetTopSongs() = launchGetTopSongsTask()

    fun onClickRetryGetPlaylists() = launchGetPlaylistsTask()

    fun onClickRetryGetRandomSongs() = launchGetRandomSongsTask()

    private fun launchGetTopSongsTask() = viewModelScope.launch(Dispatchers.IO) {
        mutState.update { it.copy(topSongs = FetchStatus.Loading) }
        var retryMs = 1000L
        while (state.value.topSongs !is FetchStatus.Ready) {
            songsRepo.getTopSongs(
                size = 5,
                onResponse = { res ->
                    Log.d(TAG, "Get top songs returned: ${res.code} ${res.msg}")
                    if (res.codeClass != SongsResponse.DataList.Code.SUCCESS) {
                        return@getTopSongs
                    }
                    mutState.update { it.copy(topSongs = FetchStatus.Ready(res.data!!)) }
                },
                onFailure = { e ->
                    Log.d(TAG, "Get top songs not sent: ${e.cause} ${e.message}")
                }
            )
            delay(1000L)
            retryMs *= 2
        }
    }

    private fun launchGetPlaylistsTask() = viewModelScope.launch(Dispatchers.IO) {
        mutState.update { it.copy(playlists = FetchStatus.Loading) }
        var retryMs = 1000L
        while (state.value.playlists !is FetchStatus.Ready) {
            playlistsRepo.getRandomPlaylists(
                size = 8,
                onResponse = { res ->
                    Log.d(TAG, "Get playlists returned: ${res.code} ${res.msg}")
                    if (res.codeClass != PlaylistsResponse.DataList.Code.SUCCESS) {
                        return@getRandomPlaylists
                    }
                    mutState.update { it.copy(playlists = FetchStatus.Ready(res.data!!)) }
                },
                onFailure = { e ->
                    Log.d(TAG, "Get playlists not sent: ${e.cause} ${e.message}")
                }
            )
            delay(1000L)
            retryMs *= 2
        }
    }

    private fun launchGetRandomSongsTask() = viewModelScope.launch(Dispatchers.IO) {
        mutState.update { it.copy(exploreSongs = FetchStatus.Loading) }
        var retryMs = 1000L
        while (state.value.exploreSongs !is FetchStatus.Ready) {
            songsRepo.getRandomSongs(
                size = 20,
                onResponse = { res ->
                    Log.d(TAG, "Get random songs returned: ${res.code} ${res.msg}")
                    if (res.codeClass != SongsResponse.DataList.Code.SUCCESS) {
                        return@getRandomSongs
                    }
                    mutState.update { it.copy(exploreSongs = FetchStatus.Ready(res.data!!)) }
                },
                onFailure = { e ->
                    Log.d(TAG, "Get random songs not sent: ${e.cause} ${e.message}")
                }
            )
            delay(1000L)
            retryMs *= 2
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                HomeScreenViewModel(
                    songsRepo = app.container.songsRepo,
                    playlistsRepo = app.container.playlistsRepo,
                    likeNotifier = app.container.likedSongsNotifierRepo
                )
            }
        }
    }
}