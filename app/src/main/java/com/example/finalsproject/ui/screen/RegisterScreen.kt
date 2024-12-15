package com.example.finalsproject.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.finalsproject.R
import com.example.finalsproject.ui.navhost.NavRoutes
import com.example.finalsproject.ui.viewmodel.RegisterNavAction
import com.example.finalsproject.ui.viewmodel.RegisterScreenViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterScreenViewModel,
    navToConfirmation: (NavRoutes.Confirmation.Arg) -> Unit,
    navToLogin: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    when (state.navAction) {
        RegisterNavAction.CONFIRMATION ->
            navToConfirmation(
                NavRoutes.Confirmation.Arg(password = state.passwordFieldValue.text)
            )

        RegisterNavAction.NONE -> {}
    }
    SideEffect {
        viewModel.resetNavAction()
    }
    AuthScreen(title = stringResource(R.string.register)) {
        RegisterFields(viewModel = viewModel)
        ErrorMessage(
            msg = when {
                state.serverError -> stringResource(R.string.internal_server_error)
                state.lastRequestFailed -> stringResource(R.string.request_not_sent)
                else -> null
            }
        )
        RegisterButtonRow(
            onClickRegister = viewModel::onClickRegister,
            hasOngoingTask = state.hasOngoingTask
        )
        NavToLoginRow(navToLogin = navToLogin)
        if (state.hasOngoingTask) {
            LocalFocusManager.current.clearFocus()
        }
    }
}

@Composable
private fun RegisterFields(viewModel: RegisterScreenViewModel) {
    val state by viewModel.state.collectAsState()
    AuthScreen.GenericField(
        value = state.usernameFieldValue,
        onValueChange = viewModel::onUsernameChange,
        label = stringResource(R.string.username),
        leadingIcon = Icons.Rounded.AccountCircle,
        keyboardType = KeyboardType.Ascii,
        errorMessage = if (state.usernameError) stringResource(R.string.username_tip) else null
    )
    AuthScreen.GenericField(
        value = state.emailFieldValue,
        onValueChange = viewModel::onEmailChange,
        label = stringResource(R.string.email),
        leadingIcon = Icons.Rounded.Mail,
        keyboardType = KeyboardType.Email,
        errorMessage = if (state.emailError) stringResource(R.string.email_tip) else null
    )
    AuthScreen.PasswordField(
        value = state.passwordFieldValue,
        onValueChange = viewModel::onPasswordChange,
        label = stringResource(R.string.password),
        errorMessage = if (state.passwordError) stringResource(R.string.password_tip) else null
    )
    AuthScreen.PasswordField(
        value = state.confirmPasswordFieldValue,
        onValueChange = viewModel::onConfirmPasswordChange,
        label = stringResource(R.string.confirm_password),
        errorMessage = if (state.confirmPasswordError) {
            stringResource(R.string.confirm_password_tip)
        } else {
            null
        },
        isLast = true
    )
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
private fun RegisterButtonRow(
    onClickRegister: () -> Unit,
    hasOngoingTask: Boolean,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onClickRegister,
            enabled = !hasOngoingTask,
            modifier = Modifier.animateContentSize()
        ) {
            Text(text = stringResource(R.string.register))
        }
        if (hasOngoingTask) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun NavToLoginRow(navToLogin: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = stringResource(R.string.already_have_an_account))
        TextButton(onClick = navToLogin) {
            Text(text = stringResource(R.string.login))
        }
    }
}