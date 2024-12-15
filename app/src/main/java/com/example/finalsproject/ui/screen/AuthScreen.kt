package com.example.finalsproject.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.finalsproject.R
import com.example.finalsproject.ui.screen.AuthScreen.PasswordField

@Composable
fun AuthScreen(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = R.drawable.register_screen_bg,
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                colorFilter = ColorFilter.tint(
                    color = Color(1f, 1f, 1f, 0.5f),
                    blendMode = BlendMode.DstAtop
                )
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(innerPadding)
                    .animateContentSize()
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = "AppName",
                    style = MaterialTheme.typography.displayLarge.copy(
                        shadow = Shadow(
                            color = Color(0x77000000),
                            offset = Offset(6f, 6f),
                            blurRadius = 3f
                        )
                    )
                )
                ElevatedCard(
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.elevatedCardColors().copy(
                        containerColor = CardDefaults.elevatedCardColors().containerColor.copy(
                            alpha = 0.75f
                        )
                    )
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge
                        )
                        content()
                    }
                }
            }
        }
    }
}

object AuthScreen {
    @Composable
    fun GenericField(
        value: TextFieldValue,
        onValueChange: (TextFieldValue) -> Unit,
        label: String,
        leadingIcon: ImageVector,
        keyboardType: KeyboardType,
        modifier: Modifier = Modifier,
        errorMessage: String? = null
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(text = label)
            },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (value.text.isNotEmpty()) {
                    IconButton(
                        onClick = { onValueChange(TextFieldValue()) }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = stringResource(R.string.clear_text_field)
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = keyboardType,
                imeAction = ImeAction.Next
            ),
            isError = errorMessage != null,
            supportingText = if (errorMessage != null) {
                { Text(text = errorMessage) }
            } else {
                null
            },
            shape = CircleShape,
            modifier = modifier.width(320.dp)
        )
    }

    @Composable
    fun PasswordField(
        value: TextFieldValue,
        onValueChange: (TextFieldValue) -> Unit,
        label: String,
        modifier: Modifier = Modifier,
        errorMessage: String? = null,
        isLast: Boolean = false
    ) {
        var showPassword by remember { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(text = label)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { showPassword = !showPassword }
                ) {
                    Icon(
                        imageVector = if (showPassword) {
                            Icons.Rounded.VisibilityOff
                        } else {
                            Icons.Rounded.Visibility
                        },
                        contentDescription = stringResource(R.string.clear_text_field)
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = if (isLast) ImeAction.Done else ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onDone = if (isLast) {
                    { focusManager.clearFocus() }
                } else {
                    null
                }
            ),
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            isError = errorMessage != null,
            supportingText = if (errorMessage != null) {
                { Text(text = errorMessage) }
            } else {
                null
            },
            shape = CircleShape,
            modifier = modifier.width(320.dp)
        )
    }
}

@Preview
@Composable
private fun UsernameFieldPreview() {
    var value by remember { mutableStateOf(TextFieldValue()) }
    AuthScreen.GenericField(
        value = value,
        onValueChange = { value = it },
        label = "Username",
        leadingIcon = Icons.Rounded.AccountCircle,
        keyboardType = KeyboardType.Ascii,
        errorMessage = null
    )
}

@Preview
@Composable
private fun PasswordFieldPreview() {
    var value by remember { mutableStateOf(TextFieldValue()) }
    PasswordField(
        value = value,
        onValueChange = { value = it },
        label = "Password",
        errorMessage = null
    )
}

@Preview
@Composable
private fun AuthScreenPreview() {
    var value by remember { mutableStateOf(TextFieldValue()) }
    AuthScreen(title = "Title") {
        PasswordField(
            value = value,
            onValueChange = { value = it },
            label = "Password",
            errorMessage = null
        )
    }
}