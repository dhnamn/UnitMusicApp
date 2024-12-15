package com.example.finalsproject.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.finalsproject.App
import com.example.finalsproject.data.CredentialsRepo
import com.example.finalsproject.data.UsersRepo
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.User
import com.example.finalsproject.model.apiResponse.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ProfileScreenViewModel"

data class ProfileScreenState(
    val user: FetchStatus<User> = FetchStatus.Idle,
    val generalStatus: FetchStatus<String> = FetchStatus.Idle,
)

class ProfileScreenViewModel(
    private val usersRepo: UsersRepo,
    private val credentialsRepo: CredentialsRepo,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileScreenState())
    val state = _state.asStateFlow()

    init {
        launchGetUserInfoTask()
    }

    fun uploadAvatar(data: ByteArray, extension: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (state.value.generalStatus == FetchStatus.Loading) {
                return@launch
            }
            _state.update { it.copy(generalStatus = FetchStatus.Loading) }
            usersRepo.uploadUserAvatar(
                data = data,
                extension = extension,
                onResponse = { res ->
                    Log.d(TAG, "Upload avatar returned: ${res.code} ${res.msg}")
                    _state.update { it.copy(generalStatus = FetchStatus.Ready(res.msg)) }
                    if (res.codeClass == UserResponse.Info.Code.SUCCESS) {
                        launchGetUserInfoTask()
                    }
                },
            ) { e ->
                Log.d(TAG, "Upload avatar not sent: ${e.cause} ${e.message}")
                _state.update { it.copy(generalStatus = FetchStatus.Failed) }
            }
        }
    }

    private fun launchGetUserInfoTask() = viewModelScope.launch(Dispatchers.IO) {
        var retryMs = 1000L
        _state.update {
            it.copy(user = FetchStatus.Loading)
        }
        while (state.value.user !is FetchStatus.Ready) {
            usersRepo.getUserInfo(
                onResponse = { res ->
                    Log.d(TAG, "Get user info returned: ${res.code} ${res.msg}")
                    if (res.codeClass != UserResponse.Info.Code.SUCCESS) {
                        return@getUserInfo
                    }
                    _state.update {
                        it.copy(user = FetchStatus.Ready(res.data!!))
                    }
                }
            ) { e ->
                Log.d(TAG, "Get user info not sent: ${e.cause} ${e.message}")
            }
            delay(retryMs)
            retryMs *= 2
        }
    }

    fun clearGeneralStatus() {
        viewModelScope.launch {
            _state.update { it.copy(generalStatus = FetchStatus.Idle) }
        }
    }

    fun logOut() {
        viewModelScope.launch(Dispatchers.IO) {
            credentialsRepo.setToken("")
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as App
                ProfileScreenViewModel(
                    usersRepo = app.container.usersRepo,
                    credentialsRepo = app.container.credentialsRepo
                )
            }
        }
    }
}