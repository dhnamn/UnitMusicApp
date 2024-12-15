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
import com.example.finalsproject.model.UserPlaylist
import com.example.finalsproject.model.apiResponse.UserPlaylistResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "UserPlaylistListViewModel"

data class UserPlaylistListScreenState(
    val userPlaylists: FetchStatus<List<UserPlaylist>> = FetchStatus.Idle
)

class UserPlaylistListViewModel(
    private val usersRepo: UsersRepo
) : ViewModel() {
    private val _state = MutableStateFlow(UserPlaylistListScreenState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(userPlaylists = FetchStatus.Loading) }
            var retryMs = 1000L
            while (state.value.userPlaylists !is FetchStatus.Ready) {
                usersRepo.getAllUserPlaylist(
                    onResponse = { res ->
                        Log.d(TAG, "Get all playlists returned ${res.code} ${res.msg}")
                        if (res.codeClass != UserPlaylistResponse.DataList.Code.SUCCESS) {
                            return@getAllUserPlaylist
                        }
                        _state.update { it.copy(userPlaylists = FetchStatus.Ready(res.data!!)) }
                    },
                ) { e ->
                    Log.d(TAG, "Get all playlists not sent: ${e.cause} ${e.message}")
                }
                delay(retryMs)
                retryMs *= 2
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App
                UserPlaylistListViewModel(usersRepo = app.container.usersRepo)
            }
        }
    }
}