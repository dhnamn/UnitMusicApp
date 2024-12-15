package com.example.finalsproject.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.finalsproject.App
import com.example.finalsproject.data.LikedSongsNotifierRepo
import com.example.finalsproject.data.SongsRepo
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.Song
import com.example.finalsproject.model.apiResponse.SongsResponse
import com.example.finalsproject.utils.updateLike
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "LeaderboardViewModel"

data class LeaderboardState(
    val topSongs: FetchStatus<List<Song>> = FetchStatus.Idle,
)

class LeaderboardViewModel(
    private val songsRepo: SongsRepo,
    val likeNotifier: LikedSongsNotifierRepo,
) : ViewModel() {
    private val mutState = MutableStateFlow(LeaderboardState())
    val state = mutState.asStateFlow()

    init {
        viewModelScope.launch {
            likeNotifier.likeEvent.collect { event ->
                mutState.value.run {
                    if (topSongs is FetchStatus.Ready) {
                        val updated = topSongs.data.updateLike(event)
                        mutState.update { it.copy(topSongs = FetchStatus.Ready(updated)) }
                    }
                }
            }
        }
        launchGetTopSongsTask()
    }

    fun onClickRetryGetTopSongs() = launchGetTopSongsTask()

    private fun launchGetTopSongsTask() = viewModelScope.launch {
        val subTag = "$TAG/GetTopSongs"
        mutState.update { it.copy(topSongs = FetchStatus.Loading) }
        Log.d(subTag, "Getting")
        songsRepo.getTopSongs(
            size = 20,
            onResponse = { res ->
                Log.d(TAG + subTag, "Response: ${res.code}: ${res.msg}")
                val songs = if (res.codeClass == SongsResponse.DataList.Code.SUCCESS) {
                    FetchStatus.Ready(checkNotNull(res.data))
                } else {
                    FetchStatus.Failed
                }
                mutState.update { it.copy(topSongs = songs) }
            },
            onFailure = { e ->
                Log.d(subTag, "No response: ${e.cause}: ${e.message}")
                mutState.update { it.copy(topSongs = FetchStatus.Failed) }
            }
        )
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                LeaderboardViewModel(
                    songsRepo = app.container.songsRepo,
                    likeNotifier = app.container.likedSongsNotifierRepo
                )
            }
        }
    }
}