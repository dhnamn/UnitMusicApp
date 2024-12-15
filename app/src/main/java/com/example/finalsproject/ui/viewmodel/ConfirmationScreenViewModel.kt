package com.example.finalsproject.ui.viewmodel

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.finalsproject.App
import com.example.finalsproject.data.AuthRepo
import com.example.finalsproject.data.CredentialsRepo
import com.example.finalsproject.model.apiRequest.AuthRequest
import com.example.finalsproject.model.apiResponse.AuthResponse
import com.example.finalsproject.ui.navhost.NavRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ConfirmationScreenViewModel"

data class ConfirmationScreenState(
    val password: String = "",

    val otpFieldValue: TextFieldValue = TextFieldValue(),

    val hasOngoingTask: Boolean = false,
    val errorMessage: String? = null,
    val serverError: Boolean = false,

    val activated: Boolean = false,
)

class ConfirmationScreenViewModel(
    savedStateHandle: SavedStateHandle,
    val authRepo: AuthRepo,
    val credentialsRepo: CredentialsRepo
) : ViewModel() {
    private val mutState = MutableStateFlow(ConfirmationScreenState())
    val state = mutState.asStateFlow()

    init {
        val arg = NavRoutes.Confirmation.getArg(savedStateHandle)!!
        mutState.update { it.copy(password = arg.password) }
    }

    fun onOtpChange(value: TextFieldValue) {
        if (state.value.hasOngoingTask) {
            return
        }
        if (value.text.length <= 6) {
            mutState.update {
                it.copy(
                    otpFieldValue = value,
                    errorMessage = null,
                    serverError = false
                )
            }
        }
        if (value.text.length == 6) {
            sendConfirmation()
        }
    }

    fun onClickResend() {
        if (state.value.hasOngoingTask) {
            return
        }
        mutState.update { it.copy(hasOngoingTask = true) }
        viewModelScope.launch {
            Log.d(TAG, "Requesting code resend")
            authRepo.postLogin(
                body = AuthRequest.Login(
                    usernameOrEmail = credentialsRepo.getAccount()!!,
                    password = state.value.password
                ),
                onResponse = { onResendResponse(res = it) }
            ) { e ->
                Log.d(
                    TAG, "Response not received. Cause: ${e::class.simpleName}: ${e.message}"
                )
                mutState.update { it.copy(errorMessage = e.message) }
            }
            mutState.update { it.copy(hasOngoingTask = false) }
        }
    }

    private fun sendConfirmation() {
        mutState.update { it.copy(hasOngoingTask = true) }
        viewModelScope.launch {
            Log.d(TAG, "Sending confirmation code")
            authRepo.postConfirmation(
                AuthRequest.Confirmation(
                    usernameOrEmail = credentialsRepo.getAccount()!!,
                    otp = state.value.otpFieldValue.text
                ), onResponse = ::onConfirmationResponse
            ) { e ->
                Log.d(
                    TAG, "Response not received. Cause: ${e::class.simpleName}: ${e.message}"
                )
                mutState.update { it.copy(errorMessage = e.message) }
            }
            mutState.update { it.copy(hasOngoingTask = false) }
        }
    }

    private fun onConfirmationResponse(res: AuthResponse.Confirmation) {
        Log.d(TAG, "Response returned: ${res.code}: ${res.msg}")
        when (res.code) {
            res.codeSuccess -> mutState.update { it.copy(activated = true) }

            res.codeIncorrect, res.codeExpired -> mutState.update {
                it.copy(errorMessage = res.msg)
            }

            else -> mutState.update { it.copy(serverError = true) }
        }
    }

    private fun onResendResponse(res: AuthResponse.Login) {
        Log.d(TAG, "Response returned: ${res.code}: ${res.msg}")
        when (res.code) {
            res.codeNeedActivation -> {}

            else -> mutState.update { it.copy(serverError = true) }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = get(APPLICATION_KEY) as App
                ConfirmationScreenViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    authRepo = app.container.authRepo,
                    credentialsRepo = app.container.credentialsRepo
                )
            }
        }
    }
}