package com.example.finalsproject.ui.composable

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finalsproject.R
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.ui.LocalNewPlaylistViewModel
import com.example.finalsproject.ui.viewmodel.NewPlaylistViewModel

@Composable
fun NewPlaylistDialog(
    viewModel: NewPlaylistViewModel = LocalNewPlaylistViewModel.current,
    onDismiss: () -> Unit,
    onCreation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        ElevatedCard(
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
            modifier = modifier
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = stringResource(R.string.new_playlist),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = state.title,
                    onValueChange = viewModel::onTitleChange,
                    label = {
                        Text(text = stringResource(R.string.title))
                    }
                )
                OutlinedTextField(
                    value = state.description,
                    onValueChange = viewModel::onDescriptionChange,
                    label = {
                        Text(text = stringResource(R.string.description))
                    }
                )
                Button(onClick = viewModel::submit) {
                    Text(text = stringResource(R.string.create))
                }
            }

            val context = LocalContext.current
            var currentToast by remember { mutableStateOf<Toast?>(null) }
            LaunchedEffect(state.status) {
                val text = when (val status = state.status) {
                    FetchStatus.Failed -> context.getString(R.string.request_not_sent)
                    is FetchStatus.Ready -> status.data
                    else -> null
                }
                if (text == null) {
                    return@LaunchedEffect
                }
                currentToast?.cancel()
                currentToast = Toast.makeText(context, text, Toast.LENGTH_SHORT).apply { show() }
            }
            LaunchedEffect(state.succeeded) {
                if (state.succeeded) {
                    onDismiss()
                    onCreation()
                }
            }
        }
    }
}