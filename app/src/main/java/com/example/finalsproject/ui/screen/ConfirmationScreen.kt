package com.example.finalsproject.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.finalsproject.R
import com.example.finalsproject.ui.viewmodel.ConfirmationScreenViewModel

@Composable
fun ConfirmationScreen(
    viewModel: ConfirmationScreenViewModel,
    navToLogin: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    if (state.activated) {
        ConfirmationSuccessPopup(onClickNavToLogin = navToLogin)
    }
    AuthScreen(title = stringResource(R.string.confirmation)) {
        if (state.hasOngoingTask) {
            LocalFocusManager.current.clearFocus()
        }
        Text(
            text = stringResource(R.string.confirmation_description),
            modifier = Modifier.width(300.dp)
        )
        OtpField(value = state.otpFieldValue, onValueChange = viewModel::onOtpChange)
        ErrorMessage(
            msg = when {
                state.serverError -> stringResource(R.string.internal_server_error)
                state.errorMessage != null -> state.errorMessage
                else -> null
            }
        )
        ResendButtonRow(
            onClickResend = viewModel::onClickResend,
            hasOngoingTask = state.hasOngoingTask
        )
    }
}

@Composable
private fun OtpField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
) {
    val focusOtpField = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusOtpField.requestFocus() }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.None
        ),
        modifier = Modifier.focusRequester(focusOtpField)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(6) {
                OtpDigit(text = if (it < value.text.length) "${value.text[it]}" else "")
            }
        }
    }
}

@Composable
private fun OtpDigit(text: String) {
    ElevatedCard(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .width(48.dp),
        colors = CardDefaults.elevatedCardColors()
            .copy(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .height(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                )
        )
    }
}

@Composable
private fun ErrorMessage(msg: String?) {
    AnimatedVisibility(msg != null) {
        Text(
            text = "${stringResource(R.string.error)}: $msg",
            style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.error),
            modifier = Modifier.width(300.dp)
        )
    }
}

@Composable
private fun ResendButtonRow(
    onClickResend: () -> Unit,
    hasOngoingTask: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onClickResend, enabled = !hasOngoingTask) {
            Text(text = stringResource(R.string.resend))
        }
        if (hasOngoingTask) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun ConfirmationSuccessPopup(onClickNavToLogin: () -> Unit) {
    Popup(
        properties = PopupProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        alignment = Alignment.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black.copy(alpha = 0.5f))
        ) {
            ElevatedCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(text = stringResource(R.string.account_activated))
                    Button(onClick = onClickNavToLogin) {
                        Text(text = stringResource(R.string.login))
                    }
                }
            }
        }
    }
}