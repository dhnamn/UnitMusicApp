package com.example.finalsproject.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.finalsproject.App
import com.example.finalsproject.data.LikedSongsNotifierRepo
import com.example.finalsproject.data.UsersRepo
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.Song
import com.example.finalsproject.model.UserPlaylist
import com.example.finalsproject.model.apiResponse.UserPlaylistResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "SongViewModel"

data class SongViewModelState(
    val userPlaylists: FetchStatus<List<UserPlaylist>> = FetchStatus.Idle,
    val addStatus: FetchStatus<String> = FetchStatus.Idle
)

class SongViewModel(
    private val usersRepo: UsersRepo,
    private val likeNotifier: LikedSongsNotifierRepo,
    private val musicQueueViewModel: MusicQueueViewModel
) : ViewModel() {
    private val _state = MutableStateFlow(SongViewModelState())
    val state = _state.asStateFlow()

    fun play(song: Song) {
        musicQueueViewModel.jumpTo(song)
    }

    fun toggleLike(song: Song) {
        likeNotifier.toggleLike(song)
    }

    fun enqueue(song: Song) {
        musicQueueViewModel.add(song)
    }

    fun loadUserPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(userPlaylists = FetchStatus.Loading) }
            var retryMs = 1000L
            while (state.value.userPlaylists !is FetchStatus.Ready) {
                usersRepo.getAllUserPlaylist(
                    onResponse = { res ->
                        Log.d(TAG, "Get all playlist returned: ${res.code} ${res.msg}")
                        if (res.codeClass != UserPlaylistResponse.DataList.Code.SUCCESS) {
                            return@getAllUserPlaylist
                        }
                        _state.update { it.copy(userPlaylists = FetchStatus.Ready(res.data!!)) }
                    },
                ) { e ->
                    Log.d(TAG, "Get all playlist not sent: ${e.cause} ${e.message}")
                }
                delay(retryMs)
                retryMs *= 2
            }
        }
    }

    fun addToPlaylist(song: Song, playlist: UserPlaylist) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(addStatus = FetchStatus.Loading) }
            usersRepo.addSongToUserPlaylist(
                songId = song.id,
                playlistId = playlist.id,
                onResponse = { res ->
                    Log.d(TAG, "Add to playlist returned: ${res.code} ${res.msg}")
                    val result = when (res.codeClass) {
                        UserPlaylistResponse.Add.Code.SUCCESS,
                        UserPlaylistResponse.Add.Code.BAD_REQUEST -> FetchStatus.Ready(res.msg)

                        else -> FetchStatus.Failed
                    }
                    _state.update { it.copy(addStatus = result) }
                }
            ) { e ->
                Log.d(TAG, "Add to playlist not sent: ${e.cause} ${e.message}")
                _state.update { it.copy(addStatus = FetchStatus.Failed) }
            }
        }
    }

    fun clearAddStatus() {
        viewModelScope.launch {
            _state.update { it.copy(addStatus = FetchStatus.Idle) }
        }
    }

    companion object {
        fun Factory(musicQueueViewModel: MusicQueueViewModel) = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App
                SongViewModel(
                    usersRepo = app.container.usersRepo,
                    likeNotifier = app.container.likedSongsNotifierRepo,
                    musicQueueViewModel = musicQueueViewModel
                )
            }
        }
    }
}