package com.example.finalsproject.ui.viewmodel

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.finalsproject.App
import com.example.finalsproject.data.AuthRepo
import com.example.finalsproject.data.CredentialsRepo
import com.example.finalsproject.model.apiRequest.AuthRequest
import com.example.finalsproject.model.apiResponse.AuthResponse
import com.example.finalsproject.validation.PasswordValidation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "LoginScreenViewModel"

enum class LoginNavAction { NONE, CONFIRMATION, HOME_SCREEN }

data class LoginScreenState(
    val accountFieldValue: TextFieldValue = TextFieldValue(),
    val passwordFieldValue: TextFieldValue = TextFieldValue(),

    val hasOngoingTask: Boolean = false,
    val lastRequestFailed: Boolean = false,
    val serverError: Boolean = false,

    val navAction: LoginNavAction = LoginNavAction.NONE
)

class LoginScreenViewModel(
    private val authRepo: AuthRepo,
    private val credentialsRepo: CredentialsRepo
) : ViewModel() {
    private val mutState = MutableStateFlow(LoginScreenState())
    val state = mutState.asStateFlow()

    init {
        viewModelScope.launch {
            mutState.update {
                it.copy(
                    accountFieldValue = TextFieldValue(credentialsRepo.getAccount() ?: "")
                )
            }
        }
    }

    fun resetNavAction() {
        mutState.update {
            it.copy(navAction = LoginNavAction.NONE)
        }
    }

    fun onUsernameOrEmailChange(value: TextFieldValue) {
        if (state.value.hasOngoingTask) {
            return
        }
        mutState.update {
            it.copy(
                accountFieldValue = value,
                lastRequestFailed = false,
                serverError = false
            )
        }
    }

    fun onPasswordChange(value: TextFieldValue) {
        if (state.value.hasOngoingTask) {
            return
        }
        if (value.text.length <= PasswordValidation.MAX_LENGTH) {
            mutState.update {
                it.copy(
                    passwordFieldValue = value,
                    lastRequestFailed = false,
                    serverError = false
                )
            }
        }
    }

    fun onClickLogin() {
        if (state.value.hasOngoingTask) {
            return
        }
        mutState.update { it.copy(hasOngoingTask = true) }
        viewModelScope.launch {
            Log.d(TAG, "Requesting login")
            authRepo.postLogin(
                body = AuthRequest.Login(
                    usernameOrEmail = state.value.accountFieldValue.text,
                    password = state.value.passwordFieldValue.text
                ),
                onResponse = ::onLoginResponse
            ) { e ->
                Log.d(
                    TAG, "Response not received. Cause: ${e::class.simpleName}: ${e.message}"
                )
                mutState.update { it.copy(lastRequestFailed = true) }
            }
            mutState.update { it.copy(hasOngoingTask = false) }
        }
    }

    private fun onLoginResponse(res: AuthResponse.Login) {
        Log.d(TAG, "Response received: ${res.code}: ${res.msg}")
        when (res.code) {
            res.codeSuccess -> {
                mutState.update { it.copy(navAction = LoginNavAction.HOME_SCREEN) }
                viewModelScope.launch {
                    credentialsRepo.setAccount(state.value.accountFieldValue.text)
                    credentialsRepo.setToken(res.token!!)
                }
            }

            res.codeNeedActivation -> {
                mutState.update { it.copy(navAction = LoginNavAction.CONFIRMATION) }
                viewModelScope.launch {
                    credentialsRepo.setAccount(state.value.accountFieldValue.text)
                }
            }

            res.codeWrongCredentials -> mutState.update { it.copy(lastRequestFailed = true) }

            else -> mutState.update { it.copy(serverError = true) }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = get(APPLICATION_KEY) as App
                LoginScreenViewModel(
                    authRepo = app.container.authRepo,
                    credentialsRepo = app.container.credentialsRepo
                )
            }
        }
    }
}