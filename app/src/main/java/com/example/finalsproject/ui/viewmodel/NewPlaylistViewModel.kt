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
import com.example.finalsproject.model.apiRequest.UserPlaylistRequest
import com.example.finalsproject.model.apiResponse.UserPlaylistResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "NewPlaylistViewModel"

data class NewPlaylistState(
    val title: String = "",
    val description: String = "",
    val status: FetchStatus<String> = FetchStatus.Idle,
    val succeeded: Boolean = false
)

class NewPlaylistViewModel(
    private val usersRepo: UsersRepo
) : ViewModel() {
    private val _state = MutableStateFlow(NewPlaylistState())
    val state = _state.asStateFlow()

    fun onTitleChange(value: String) {
        viewModelScope.launch {
            _state.update { it.copy(title = value) }
        }
    }

    fun onDescriptionChange(value: String) {
        viewModelScope.launch {
            _state.update { it.copy(description = value) }
        }
    }

    fun submit() {
        viewModelScope.launch {
            _state.update { it.copy(status = FetchStatus.Loading) }
            usersRepo.createPlaylist(
                body = UserPlaylistRequest.Create(
                    title = state.value.title.ifBlank { "New playlist" },
                    description = state.value.description
                ),
                onResponse = { res ->
                    Log.d(TAG, "Create playlist returned: ${res.code} ${res.msg}")
                    _state.update {
                        it.copy(
                            status = FetchStatus.Ready(res.msg),
                            succeeded = res.codeClass == UserPlaylistResponse.Create.Code.SUCCESS
                        )
                    }
                },
            ) { e ->
                Log.d(TAG, "Create playlist not sent: ${e.cause} ${e.message}")
                _state.update { it.copy(status = FetchStatus.Failed) }
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App
                NewPlaylistViewModel(usersRepo = app.container.usersRepo)
            }
        }
    }
}