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
import com.example.finalsproject.validation.EmailValidation
import com.example.finalsproject.validation.PasswordValidation
import com.example.finalsproject.validation.UsernameValidation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "RegisterScreenViewModel"

enum class RegisterNavAction { NONE, CONFIRMATION }

data class RegisterScreenState(
    val usernameFieldValue: TextFieldValue = TextFieldValue(),
    val usernameError: Boolean = false,

    val emailFieldValue: TextFieldValue = TextFieldValue(),
    val emailError: Boolean = false,

    val passwordFieldValue: TextFieldValue = TextFieldValue(),
    val passwordError: Boolean = false,

    val confirmPasswordFieldValue: TextFieldValue = TextFieldValue(),
    val confirmPasswordError: Boolean = false,

    val hasOngoingTask: Boolean = false,
    val lastRequestFailed: Boolean = false,
    val serverError: Boolean = false,

    val navAction: RegisterNavAction = RegisterNavAction.NONE,
)

class RegisterScreenViewModel(
    private val authRepo: AuthRepo,
    private val credentialsRepo: CredentialsRepo
) : ViewModel() {
    private val mutState = MutableStateFlow(RegisterScreenState())
    val state = mutState.asStateFlow()

    fun resetNavAction() {
        mutState.update {
            it.copy(navAction = RegisterNavAction.NONE)
        }
    }

    fun onUsernameChange(value: TextFieldValue) {
        if (state.value.hasOngoingTask) {
            return
        }
        if (value.text.length <= UsernameValidation.MAX_LENGTH) {
            mutState.update {
                it.copy(
                    usernameFieldValue = value,
                    usernameError = false,
                    lastRequestFailed = false,
                    serverError = false,
                )
            }
        }
    }

    fun onEmailChange(value: TextFieldValue) {
        if (state.value.hasOngoingTask) {
            return
        }
        mutState.update {
            it.copy(
                emailFieldValue = value,
                emailError = false,
                lastRequestFailed = false,
                serverError = false,
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
                    passwordError = false,
                    lastRequestFailed = false,
                    serverError = false,
                )
            }
        }
    }

    fun onConfirmPasswordChange(value: TextFieldValue) {
        if (state.value.hasOngoingTask) {
            return
        }
        if (value.text.length <= PasswordValidation.MAX_LENGTH) {
            mutState.update {
                it.copy(
                    confirmPasswordFieldValue = value,
                    confirmPasswordError = false,
                    lastRequestFailed = false,
                    serverError = false,
                )
            }
        }
    }

    fun onClickRegister() {
        if (state.value.hasOngoingTask) {
            return
        }
        mutState.update { it.copy(hasOngoingTask = true) }
        viewModelScope.launch {
            try {
                Log.d(TAG, "Register clicked")
                var isValid = true
                if (!UsernameValidation.isValid(state.value.usernameFieldValue.text)) {
                    mutState.update { it.copy(usernameError = true) }
                    isValid = false
                }
                if (!EmailValidation.isValid(state.value.emailFieldValue.text)) {
                    mutState.update { it.copy(emailError = true) }
                }
                if (!PasswordValidation.isValid(state.value.passwordFieldValue.text)) {
                    mutState.update { it.copy(passwordError = true) }
                    isValid = false
                }
                if (
                    state.value.passwordFieldValue.text !=
                    state.value.confirmPasswordFieldValue.text
                ) {
                    mutState.update { it.copy(confirmPasswordError = true) }
                    isValid = false
                }
                if (!isValid) {
                    Log.d(TAG, "Fields not valid")
                    return@launch
                }
                Log.d(TAG, "Start registering")
                authRepo.postRegister(
                    AuthRequest.Register(
                        username = state.value.usernameFieldValue.text,
                        email = state.value.emailFieldValue.text,
                        password = state.value.passwordFieldValue.text
                    ),
                    onSuccess = ::onRegisterResponse
                ) { e ->
                    Log.d(
                        TAG, "Response not received. Cause: ${e::class.simpleName}: ${e.message}"
                    )
                    mutState.update { it.copy(lastRequestFailed = true) }
                }
            } finally {
                mutState.update { it.copy(hasOngoingTask = false) }
            }
        }
    }

    private fun onRegisterResponse(res: AuthResponse.Register) {
        Log.d(TAG, "Response returned: ${res.code}: ${res.msg}")
        when (res.code) {
            res.codeSuccess -> {
                mutState.update { it.copy(navAction = RegisterNavAction.CONFIRMATION) }
                viewModelScope.launch {
                    credentialsRepo.setAccount(state.value.usernameFieldValue.text)
                }
            }

            res.codeFailure -> mutState.update { it.copy(lastRequestFailed = true) }

            else -> mutState.update { it.copy(serverError = true) }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                Log.d(TAG, "Creating ViewModel")
                val app = this[APPLICATION_KEY] as App
                RegisterScreenViewModel(
                    authRepo = app.container.authRepo,
                    credentialsRepo = app.container.credentialsRepo
                )
            }
        }
    }
}