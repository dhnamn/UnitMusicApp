package com.example.finalsproject.ui.composable

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finalsproject.R
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.Song
import com.example.finalsproject.ui.LocalSongViewModel
import com.example.finalsproject.ui.viewmodel.SongViewModel
import com.example.finalsproject.utils.Utils
import com.example.finalsproject.utils.base64ToImageBitmap

@Composable
fun SongCard(
    song: Song,
    viewModel: SongViewModel = LocalSongViewModel.current,
    modifier: Modifier = Modifier,
    extraOptions: (@Composable ColumnScope.() -> Unit)? = null,
) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        onClick = { viewModel.play(song) },
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(8.dp)
        ) {
            var bitmap by remember { mutableStateOf(ImageBitmap(1, 1)) }
            LaunchedEffect(song.albumImgBase64) {
                bitmap = Utils.base64ToImageBitmap(song.albumImgBase64)
            }
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = song.artist,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Box {
                var showMenu by remember { mutableStateOf(false) }
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = stringResource(R.string.options)
                    )
                }
                SongOptionDropdown(
                    song = song,
                    viewModel = viewModel,
                    expanded = showMenu,
                    onDismiss = { showMenu = false },
                    extraOptions = extraOptions
                )
            }
        }
    }
}

@Composable
fun SongOptionDropdown(
    song: Song,
    viewModel: SongViewModel = LocalSongViewModel.current,
    expanded: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    showFavoriteOption: Boolean = true,
    showEnqueueOption: Boolean = true,
    showAddToPlaylistOption: Boolean = true,
    extraOptions: (@Composable ColumnScope.() -> Unit)? = null,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        if (showFavoriteOption) {
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.add_to_favorites)) },
                onClick = { viewModel.toggleLike(song) },
                trailingIcon = {
                    Icon(
                        imageVector = if (song.likedByUser) {
                            Icons.Rounded.Favorite
                        } else {
                            Icons.Rounded.FavoriteBorder
                        },
                        contentDescription = null,
                        tint = if (song.likedByUser) {
                            Color.Red
                        } else {
                            LocalContentColor.current
                        }
                    )
                }
            )
        }
        if (showEnqueueOption) {
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.add_to_queue)) },
                onClick = {
                    viewModel.enqueue(song)
                    onDismiss()
                }
            )
        }
        if (showAddToPlaylistOption) {
            var showAddDialog by remember { mutableStateOf(false) }
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.add_to_playlist)) },
                onClick = {
                    showAddDialog = true
                    viewModel.loadUserPlaylists()
                }
            )
            if (showAddDialog) {
                AddSongToPlaylistDialog(
                    song = song,
                    viewModel = viewModel,
                    onDismiss = {
                        onDismiss()
                        showAddDialog = false
                        viewModel.clearAddStatus()
                    },
                )
            }
        }
        extraOptions?.let { it() }
    }
}

@Composable
fun AddSongToPlaylistDialog(
    song: Song,
    viewModel: SongViewModel = LocalSongViewModel.current,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var lastPlaylistIdAdded: Long? by remember { mutableStateOf(null) }
    var showNewPlaylistDialog by remember { mutableStateOf(false) }

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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .width(300.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.add_to_playlist),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                when (val playlists = state.userPlaylists) {
                    is FetchStatus.Ready -> {
                        items(items = playlists.data, key = { it.id }) { playlist ->
                            TextButton(
                                onClick = {
                                    lastPlaylistIdAdded = playlist.id
                                    viewModel.addToPlaylist(song, playlist)
                                },
                                shape = RectangleShape,
                                enabled = state.addStatus !is FetchStatus.Loading,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.height(52.dp)
                                ) {
                                    Text(text = playlist.title, modifier = Modifier.weight(1f))
                                    if (
                                        state.addStatus is FetchStatus.Loading &&
                                        lastPlaylistIdAdded == playlist.id
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                        item {
                            TextButton(
                                onClick = { showNewPlaylistDialog = true },
                                shape = RectangleShape,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = stringResource(R.string.new_playlist))
                                    Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                                }
                            }
                        }
                    }

                    else -> item {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            if (showNewPlaylistDialog) {
                NewPlaylistDialog(
                    onDismiss = { showNewPlaylistDialog = false },
                    onCreation = { viewModel.loadUserPlaylists() }
                )
            }

            val context = LocalContext.current
            var currentToast: Toast? by remember { mutableStateOf(null) }
            LaunchedEffect(state.addStatus) {
                val text = when (val status = state.addStatus) {
                    FetchStatus.Failed -> context.getString(R.string.request_not_sent)
                    is FetchStatus.Ready -> status.data
                    else -> null
                }
                if (text != null) {
                    currentToast?.cancel()
                    currentToast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
                        .apply { show() }
                }
            }
        }
    }
}