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
import com.example.finalsproject.model.apiResponse.UserResponse
import com.example.finalsproject.utils.updateLike
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "LikedScreenViewModel"

data class LikedScreenState(
    val songs: FetchStatus<List<Song>> = FetchStatus.Idle,
    val isLoading: Boolean = false
)

class LikedScreenViewModel(
    private val usersRepo: UsersRepo,
    private val likeNotifier: LikedSongsNotifierRepo
) : ViewModel() {
    private val _state = MutableStateFlow(LikedScreenState())
    val state = _state.asStateFlow()
    private var _currentPage = 1
    private var _totalPage = 0

    init {
        viewModelScope.launch(Dispatchers.IO) {
            var retryMs = 1000L
            while (state.value.songs !is FetchStatus.Ready) {
                usersRepo.getLikedSongs(
                    size = 15,
                    page = 1,
                    onResponse = { res ->
                        Log.d(TAG, "Get liked songs returned ${res.code} ${res.msg}")
                        if (res.codeClass == UserResponse.SongList.Code.SUCCESS) {
                            _state.update { it.copy(songs = FetchStatus.Ready(res.data!!)) }
                            _totalPage = res.totalPage!!
                        }
                    }
                ) { e ->
                    Log.d(TAG, "Get liked songs not sent ${e.cause} ${e.message}")
                }
                delay(retryMs)
                retryMs *= 2
            }
        }

        viewModelScope.launch {
            likeNotifier.likeEvent.collect { event ->
                state.value.songs.let { songs ->
                    if (songs !is FetchStatus.Ready) {
                        return@collect
                    }
                    _state.update {
                        it.copy(songs = FetchStatus.Ready(songs.data.updateLike(event)))
                    }
                }
            }
        }
    }

    fun loadMore() {
        viewModelScope.launch(Dispatchers.IO) {
            if (state.value.songs !is FetchStatus.Ready || state.value.isLoading) {
                return@launch
            }
            if (_currentPage >= _totalPage) {
                return@launch
            }
            _state.update { it.copy(isLoading = true) }
            usersRepo.getLikedSongs(
                size = 15,
                page = _currentPage + 1,
                onResponse = { res ->
                    Log.d(TAG, "Get more liked songs returned ${res.code} ${res.msg}")
                    if (res.codeClass == UserResponse.SongList.Code.SUCCESS) {
                        _currentPage++
                        val updated = (state.value.songs as FetchStatus.Ready).data + res.data!!
                        _state.update { it.copy(songs = FetchStatus.Ready(updated)) }
                        _totalPage = res.totalPage!!
                    }
                },
            ) { e ->
                Log.d(TAG, "Get more liked songs not sent ${e.cause} ${e.message}")
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App
                LikedScreenViewModel(
                    usersRepo = app.container.usersRepo,
                    likeNotifier = app.container.likedSongsNotifierRepo
                )
            }
        }
    }
}