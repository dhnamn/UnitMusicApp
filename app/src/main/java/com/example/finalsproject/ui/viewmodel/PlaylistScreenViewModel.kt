package com.example.finalsproject.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.finalsproject.App
import com.example.finalsproject.data.LikedSongsNotifierRepo
import com.example.finalsproject.data.PlaylistsRepo
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.Playlist
import com.example.finalsproject.model.apiResponse.PlaylistsResponse
import com.example.finalsproject.ui.navhost.NavRoutes
import com.example.finalsproject.utils.updateLike
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "PlaylistScreenViewModel"

data class PlaylistScreenState(
    val id: Long = -1,
    val playlistContent: FetchStatus<Playlist> = FetchStatus.Idle
)

class PlaylistScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val playlistsRepo: PlaylistsRepo,
    val likeNotifier: LikedSongsNotifierRepo
) : ViewModel() {
    private val mutState = MutableStateFlow(PlaylistScreenState())
    val state = mutState.asStateFlow()

    init {
        mutState.update {
            it.copy(id = NavRoutes.Playlist.getArg(savedStateHandle)!!.id)
        }
        viewModelScope.launch {
            likeNotifier.likeEvent.collect { event ->
                mutState.value.apply {
                    if (playlistContent !is FetchStatus.Ready) {
                        return@collect
                    }
                    val updated = playlistContent.data.copy(
                        songs = playlistContent.data.songs!!.updateLike(event)
                    )
                    mutState.update { it.copy(playlistContent = FetchStatus.Ready(updated)) }
                }
            }
        }
        launchGetPlaylistTask()
    }

    fun onClickRetryGetPlaylistTask() = launchGetPlaylistTask()

    private fun launchGetPlaylistTask() = viewModelScope.launch {
        val subTag = "$TAG/GetPlaylistTask"
        mutState.update { it.copy(playlistContent = FetchStatus.Loading) }
        for (tryCount in 1..API_RETRY_COUNT) {
            Log.d(subTag, "Getting")
            playlistsRepo.getFullPlaylistById(
                id = state.value.id,
                onResponse = { res ->
                    Log.d(subTag, "Response: ${res.code}: ${res.msg}")
                    if (res.codeClass == PlaylistsResponse.Full.Code.SUCCESS) {
                        mutState.update {
                            it.copy(
                                playlistContent = FetchStatus.Ready(
                                    res.data!!
                                )
                            )
                        }
                    }
                },
                onFailure = { e ->
                    Log.d(subTag, "No response: ${e.cause}: ${e.message}")
                }
            )

            if (state.value.playlistContent is FetchStatus.Ready) {
                break
            }
            if (tryCount == API_RETRY_COUNT) {
                mutState.update {
                    it.copy(playlistContent = FetchStatus.Failed)
                }
                break
            }
            delay(API_RETRY_INTERVAL_MS)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                PlaylistScreenViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    playlistsRepo = app.container.playlistsRepo,
                    likeNotifier = app.container.likedSongsNotifierRepo
                )
            }
        }

        private const val API_RETRY_COUNT = 5
        private const val API_RETRY_INTERVAL_MS = 1000L
    }
}