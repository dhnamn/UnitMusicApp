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
import com.example.finalsproject.data.UsersRepo
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.Song
import com.example.finalsproject.model.UserPlaylist
import com.example.finalsproject.model.apiRequest.UserPlaylistRequest
import com.example.finalsproject.model.apiResponse.UserPlaylistResponse
import com.example.finalsproject.ui.navhost.NavRoutes
import com.example.finalsproject.utils.updateLike
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "PlaylistScreenViewModel"

data class UserPlaylistState(
    val id: Long = -1,
    val playlistContent: FetchStatus<UserPlaylist> = FetchStatus.Idle,
    val status: FetchStatus<String> = FetchStatus.Idle,
)

class UserPlaylistViewModel(
    savedStateHandle: SavedStateHandle,
    private val usersRepo: UsersRepo,
    val likeNotifier: LikedSongsNotifierRepo
) : ViewModel() {
    private val mutState = MutableStateFlow(UserPlaylistState())
    val state = mutState.asStateFlow()

    init {
        mutState.update { it.copy(id = NavRoutes.UserPlaylist.getArg(savedStateHandle)!!.id) }
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

    fun onRemoveSongFromPlaylist(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            state.value.playlistContent.let { playlist ->
                if (playlist !is FetchStatus.Ready) {
                    return@launch
                }
                val idx = playlist.data.songs!!.indexOfFirst { it.id == song.id }
                if (idx == -1) {
                    return@launch
                }
                mutState.update { it.copy(status = FetchStatus.Loading) }
                usersRepo.removeSongFromUserPlaylist(
                    songId = song.id,
                    playlistId = playlist.data.id,
                    onResponse = { res ->
                        Log.d(TAG, "Remove song returned: ${res.code} ${res.msg}")
                        if (res.codeClass != UserPlaylistResponse.Remove.Code.SUCCESS) {
                            return@removeSongFromUserPlaylist
                        }
                        val updated = playlist.data.copy(
                            songs = playlist.data.songs.toMutableList().apply { removeAt(idx) }
                        )
                        mutState.update {
                            it.copy(
                                playlistContent = FetchStatus.Ready(updated),
                                status = FetchStatus.Ready(res.msg)
                            )
                        }
                    },
                ) { e ->
                    Log.d(TAG, "Remove song not sent: ${e.cause} ${e.message}")
                    mutState.update { it.copy(status = FetchStatus.Failed) }
                }
            }
        }
    }

    fun onTitleChange(value: String) {
        viewModelScope.launch {
            state.value.playlistContent.let { playlist ->
                if (playlist !is FetchStatus.Ready) {
                    return@launch
                }
                val updated = playlist.data.copy(title = value)
                mutState.update { it.copy(playlistContent = FetchStatus.Ready(updated)) }
            }
        }
    }

    fun onDescriptionChange(value: String) {
        viewModelScope.launch {
            state.value.playlistContent.let { playlist ->
                if (playlist !is FetchStatus.Ready) {
                    return@launch
                }
                val updated = playlist.data.copy(description = value)
                mutState.update { it.copy(playlistContent = FetchStatus.Ready(updated)) }
            }
        }
    }

    fun onUpdate(onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val body = state.value.playlistContent.let {
                if (it !is FetchStatus.Ready) {
                    return@launch
                }
                UserPlaylistRequest.Update(
                    title = it.data.title,
                    description = it.data.description
                )
            }
            usersRepo.updateUserPlaylist(
                playlistId = state.value.id,
                body = body,
                onResponse = { res ->
                    Log.d(TAG, "Update playlist returned: ${res.code} ${res.msg}")
                    mutState.update { it.copy(status = FetchStatus.Ready(res.msg)) }
                    if (res.codeClass == UserPlaylistResponse.Update.Code.SUCCESS) {
                        viewModelScope.launch(Dispatchers.Main) {
                            onSuccess()
                        }
                    }
                }
            ) { e ->
                Log.d(TAG, "Update playlist not sent: ${e.cause} ${e.message}")
                mutState.update { it.copy(status = FetchStatus.Failed) }
            }
        }
    }

    fun onDelete(onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if (state.value.playlistContent !is FetchStatus.Ready) {
                return@launch
            }
            usersRepo.deleteUserPlaylist(
                playlistId = state.value.id,
                onResponse = { res ->
                    Log.d(TAG, "Delete playlist returned: ${res.code} ${res.msg}")
                    mutState.update { it.copy(status = FetchStatus.Ready(res.msg)) }
                    if (res.codeClass == UserPlaylistResponse.Delete.Code.SUCCESS) {
                        viewModelScope.launch(Dispatchers.Main) {
                            onSuccess()
                        }
                    }
                },
            ) { e ->
                Log.d(TAG, "Delete playlist returned: ${e.cause} ${e.message}")
                mutState.update { it.copy(status = FetchStatus.Failed) }
            }
        }
    }

    private fun launchGetPlaylistTask() = viewModelScope.launch {
        mutState.update { it.copy(playlistContent = FetchStatus.Loading) }
        var retryMs = 1000L
        while (state.value.playlistContent !is FetchStatus.Ready) {
            Log.d(TAG, "Getting")
            usersRepo.getUserPlaylist(
                playlistId = state.value.id,
                onResponse = { res ->
                    Log.d(TAG, "Response: ${res.code}: ${res.msg}")
                    if (res.codeClass == UserPlaylistResponse.Full.Code.SUCCESS) {
                        mutState.update { it.copy(playlistContent = FetchStatus.Ready(res.data!!)) }
                    }
                },
                onFailure = { e ->
                    Log.d(TAG, "No response: ${e.cause}: ${e.message}")
                }
            )
            delay(retryMs)
            retryMs *= 2
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                UserPlaylistViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    usersRepo = app.container.usersRepo,
                    likeNotifier = app.container.likedSongsNotifierRepo
                )
            }
        }
    }
}