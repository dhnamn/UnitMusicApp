package com.example.finalsproject.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.finalsproject.R
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.Song
import com.example.finalsproject.model.UserPlaylist
import com.example.finalsproject.ui.composable.SongCard
import com.example.finalsproject.ui.viewmodel.MusicQueueViewModel
import com.example.finalsproject.ui.viewmodel.UserPlaylistViewModel
import com.example.finalsproject.utils.secondsToTimeFormat

@Composable
fun UserPlaylistScreen(
    viewModel: UserPlaylistViewModel,
    musicQueueViewModel: MusicQueueViewModel,
    navBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    var editMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (editMode) {
                TopBarEdit(
                    navBack = navBack,
                    onClickFinish = { viewModel.onUpdate { editMode = false } },
                )
            } else {
                TopBar(
                    navBack = navBack,
                    onClickEdit = { editMode = true },
                    onClickDelete = { viewModel.onDelete(onSuccess = navBack) },
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (val playlistContent = state.playlistContent) {
                is FetchStatus.Ready -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxSize()
                    ) {
                        if (editMode) {
                            PlaylistInfoSectionEdit(
                                playlist = playlistContent.data,
                                onTitleChange = viewModel::onTitleChange,
                                onDescriptionChange = viewModel::onDescriptionChange,
                            )
                        } else {
                            PlaylistInfoSection(
                                playlist = playlistContent.data,
                                onClickPlay = {
                                    musicQueueViewModel.setQueue(playlistContent.data.songs!!)
                                }
                            )
                        }
                        SongList(
                            songs = playlistContent.data.songs!!,
                            onRemoveFromPlaylist = viewModel::onRemoveSongFromPlaylist
                        )
                    }
                }

                else -> ContentLoading()
            }
        }

        val context = LocalContext.current
        var lastToast by remember { mutableStateOf<Toast?>(null) }
        LaunchedEffect(state.status) {
            val text = when (val status = state.status) {
                is FetchStatus.Ready -> status.data
                FetchStatus.Failed -> context.getString(R.string.request_not_sent)
                else -> null
            }
            if (text == null) {
                return@LaunchedEffect
            }
            lastToast?.apply { cancel() }
            lastToast = Toast.makeText(context, text, Toast.LENGTH_SHORT).apply { show() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    navBack: () -> Unit,
    onClickEdit: () -> Unit,
    onClickDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = navBack) {
                Icon(
                    imageVector = Icons.Rounded.ChevronLeft,
                    contentDescription = stringResource(R.string.navigate_back)
                )
            }
        },
        title = {
            Text(text = stringResource(R.string.playlist))
        },
        actions = {
            IconButton(onClick = onClickEdit) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = stringResource(R.string.edit)
                )
            }
            IconButton(onClick = onClickDelete) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = stringResource(R.string.delete)
                )
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarEdit(
    navBack: () -> Unit,
    onClickFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = navBack) {
                Icon(
                    imageVector = Icons.Rounded.ChevronLeft,
                    contentDescription = stringResource(R.string.navigate_back)
                )
            }
        },
        title = {
            Text(text = stringResource(R.string.playlist))
        },
        actions = {
            IconButton(onClick = onClickFinish) {
                Icon(
                    imageVector = Icons.Rounded.Done,
                    contentDescription = stringResource(R.string.edit)
                )
            }
        },
        modifier = modifier
    )
}

@Composable
private fun PlaylistInfoSection(
    playlist: UserPlaylist,
    onClickPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        PlaylistTitle(title = playlist.title)
        PlaylistDescription(description = playlist.description)
        var durationInSeconds by remember {
            mutableIntStateOf(0)
        }
        LaunchedEffect(playlist.songs!!.size) {
            durationInSeconds = playlist.songs.sumOf { it.length }
        }
        CountAndDurationText(
            count = playlist.songs.size,
            durationInSeconds = durationInSeconds,
        )
        DividerAndPlay(onClickPlay = onClickPlay)
    }
}

@Composable
private fun PlaylistInfoSectionEdit(
    playlist: UserPlaylist,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(value = playlist.title, onValueChange = onTitleChange)
        OutlinedTextField(value = playlist.description, onValueChange = onDescriptionChange)
    }
}

@Composable
private fun PlaylistTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineLarge.copy(
            shadow = Shadow(
                color = Color(0x77000000),
                offset = Offset(6f, 6f),
                blurRadius = 3f
            )
        ),
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
private fun PlaylistDescription(
    description: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = description.ifEmpty { stringResource(R.string.no_description) },
        style = MaterialTheme.typography.bodyLarge.copy(
            shadow = Shadow(
                color = Color(0x77000000),
                offset = Offset(4f, 4f),
                blurRadius = 2f
            )
        ),
        modifier = modifier
    )
}

@Composable
private fun CountAndDurationText(
    count: Int,
    durationInSeconds: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = "$count ${stringResource(R.string.songs)} - " +
                durationInSeconds.secondsToTimeFormat(),
        style = LocalTextStyle.current.copy(
            shadow = Shadow(
                color = Color(0x77000000),
                offset = Offset(4f, 4f),
                blurRadius = 2f
            )
        ),
        modifier = modifier
    )
}

@Composable
private fun DividerAndPlay(
    onClickPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Center)
        )
        IconButton(
            onClick = onClickPlay,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 40.dp)
                .size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.5f)
                        .clip(CircleShape)
                        .background(Color.White)
                )
                Icon(
                    imageVector = Icons.Rounded.PlayCircle,
                    contentDescription = stringResource(R.string.play),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun SongList(
    songs: List<Song>,
    onRemoveFromPlaylist: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        items(items = songs, key = { it.id }) {
            SongCard(song = it, modifier = Modifier.animateItem()) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.remove_from_playlist)) },
                    onClick = { onRemoveFromPlaylist(it) }
                )
            }
        }
    }
}

@Composable
private fun ContentLoading(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray)
        ) {
            CircularProgressIndicator()
        }
        Box(
            modifier = Modifier
                .width(360.dp)
                .height(40.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(40.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )
    }
}