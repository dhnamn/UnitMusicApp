package com.example.finalsproject.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
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
import com.example.finalsproject.ui.viewmodel.LoginNavAction
import com.example.finalsproject.ui.viewmodel.LoginScreenViewModel

@Composable
fun LoginScreen(
    viewModel: LoginScreenViewModel,
    navToRegister: () -> Unit,
    navToConfirmation: (NavRoutes.Confirmation.Arg) -> Unit,
    navToHome: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    when (state.navAction) {
        LoginNavAction.CONFIRMATION ->
            navToConfirmation(
                NavRoutes.Confirmation.Arg(password = state.passwordFieldValue.text)
            )

        LoginNavAction.HOME_SCREEN -> navToHome()

        LoginNavAction.NONE -> {}
    }
    SideEffect {
        viewModel.resetNavAction()
    }
    AuthScreen(title = stringResource(R.string.login)) {
        LoginFields(viewModel = viewModel)
        ErrorMessage(
            msg = when {
                state.serverError -> stringResource(R.string.internal_server_error)
                state.lastRequestFailed -> stringResource(R.string.request_not_sent)
                else -> null
            }
        )
        LoginButtonRow(
            onClickLogin = viewModel::onClickLogin,
            hasOngoingTask = state.hasOngoingTask
        )
        NavToRegisterRow(onClickNavToRegister = navToRegister)
        if (state.hasOngoingTask) {
            LocalFocusManager.current.clearFocus()
        }
    }
}

@Composable
private fun LoginFields(viewModel: LoginScreenViewModel) {
    val state by viewModel.state.collectAsState()
    AuthScreen.GenericField(
        value = state.accountFieldValue,
        onValueChange = viewModel::onUsernameOrEmailChange,
        label = stringResource(R.string.username_or_email),
        leadingIcon = Icons.Rounded.AccountCircle,
        keyboardType = KeyboardType.Ascii
    )
    AuthScreen.PasswordField(
        value = state.passwordFieldValue,
        onValueChange = viewModel::onPasswordChange,
        label = stringResource(R.string.password),
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
private fun LoginButtonRow(
    onClickLogin: () -> Unit,
    hasOngoingTask: Boolean,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onClickLogin,
            enabled = !hasOngoingTask
        ) {
            Text(text = stringResource(R.string.login))
        }
        if (hasOngoingTask) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun NavToRegisterRow(onClickNavToRegister: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = stringResource(R.string.dont_have_an_account))
        TextButton(onClick = onClickNavToRegister) {
            Text(text = stringResource(R.string.register))
        }
    }
}