package com.example.finalsproject.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.finalsproject.App
import com.example.finalsproject.data.UsersRepo
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.Song
import com.example.finalsproject.model.UserPlaylist
import com.example.finalsproject.model.apiResponse.UserPlaylistResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddToPlaylistState(
    val playlists: FetchStatus<List<UserPlaylist>> = FetchStatus.Idle,
    val addStatus: FetchStatus<String> = FetchStatus.Idle
)

class AddToPlaylistViewModel(
    private val usersRepo: UsersRepo
) : ViewModel() {
    private val _state = MutableStateFlow(AddToPlaylistState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(playlists = FetchStatus.Loading) }
            while (state.value.playlists !is FetchStatus.Ready) {
                usersRepo.getAllUserPlaylist(
                    onResponse = { res ->
                        if (res.codeClass == UserPlaylistResponse.DataList.Code.SUCCESS) {
                            _state.update { it.copy(playlists = FetchStatus.Ready(res.data!!)) }
                        }
                    },
                    onFailure = { e -> }
                )
            }
            delay(3000L)
        }
    }

    fun addToPlaylist(song: Song, userPlaylist: UserPlaylist) {
        viewModelScope.launch {
            if (state.value.addStatus is FetchStatus.Loading) {
                return@launch
            }
            _state.update { it.copy(addStatus = FetchStatus.Loading) }
            usersRepo.addSongToUserPlaylist(
                songId = song.id,
                playlistId = userPlaylist.id,
                onResponse = { res ->
                    Log.d("TEST", "${res.codeClass}")
                    val status = when (res.codeClass) {
                        UserPlaylistResponse.Add.Code.SUCCESS -> FetchStatus.Ready(res.msg)
                        else -> FetchStatus.Failed
                    }
                    _state.update { it.copy(addStatus = status) }
                },
                onFailure = { e ->
                    _state.update { it.copy(addStatus = FetchStatus.Failed) }
                }
            )
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App
                AddToPlaylistViewModel(usersRepo = app.container.usersRepo)
            }
        }
    }
}