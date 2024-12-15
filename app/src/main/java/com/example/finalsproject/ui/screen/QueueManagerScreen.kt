package com.example.finalsproject.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.finalsproject.R
import com.example.finalsproject.model.Song
import com.example.finalsproject.ui.LocalSongViewModel
import com.example.finalsproject.ui.composable.SongOptionDropdown
import com.example.finalsproject.ui.viewmodel.MusicQueueViewModel
import com.example.finalsproject.utils.Utils
import com.example.finalsproject.utils.base64ToImageBitmap
import com.example.finalsproject.utils.secondsToTimeFormat
import kotlinx.coroutines.flow.map

@Composable
fun QueueManagerScreen(
    viewModel: MusicQueueViewModel,
    navBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val currentSongStamp by viewModel.progressStateMs
        .map { it.toInt() / 1000 }
        .collectAsState(initial = 0)
    Scaffold(
        topBar = {
            var queueRemainingDuration by remember { mutableIntStateOf(0) }
            LaunchedEffect(state.currentIdx, state.queue) {
                queueRemainingDuration = state.currentQueue.let { q ->
                    if (q.isNotEmpty()) {
                        q.subList(state.currentIdx, q.size).sumOf { it.length }
                    } else 0
                }
            }
            TopBar(
                navBack = navBack,
                clearQueue = viewModel::clear,
                queueRemainingDuration = queueRemainingDuration - currentSongStamp,
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            SongList(
                songs = state.currentQueue,
                currentIdx = state.currentIdx,
                onRemoveFromQueue = { viewModel.remove(it) }
            )
        }
    }
}

@Composable
private fun TopBar(
    navBack: () -> Unit,
    clearQueue: () -> Unit,
    queueRemainingDuration: Int,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        shape = RectangleShape,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            IconButton(
                onClick = navBack,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChevronLeft,
                    contentDescription = stringResource(R.string.navigate_back)
                )
            }
            val durationString = queueRemainingDuration.secondsToTimeFormat()
            Text(
                text = "${stringResource(R.string.queue)}: $durationString",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                onClick = {
                    clearQueue()
                    navBack()
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlaylistRemove,
                    contentDescription = stringResource(R.string.remove_queue)
                )
            }
        }
    }
}

@Composable
fun SongList(
    songs: List<Song>,
    currentIdx: Int,
    onRemoveFromQueue: (Song) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        items(items = songs, key = { it.id }) {
            SongCard(
                song = it,
                isCurrent = songs[currentIdx].id == it.id,
                onRemoveFromQueue = { onRemoveFromQueue(it) }
            )
        }
    }
}

@Composable
private fun SongCard(
    song: Song,
    isCurrent: Boolean,
    onRemoveFromQueue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = LocalSongViewModel.current
    ElevatedCard(
        onClick = { viewModel.play(song) },
        colors = CardDefaults.elevatedCardColors().run {
            if (isCurrent) {
                copy(containerColor = MaterialTheme.colorScheme.primaryContainer)
            } else this
        },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
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
            val bitmap by remember {
                mutableStateOf(Utils.base64ToImageBitmap(song.albumImgBase64))
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    colorFilter = if (isCurrent) {
                        ColorFilter.tint(
                            color = Color(1f, 1f, 1f, 0.5f),
                            blendMode = BlendMode.DstAtop
                        )
                    } else null,
                    modifier = Modifier.fillMaxSize()
                )
                if (isCurrent) {
                    Icon(
                        imageVector = Icons.Rounded.Equalizer,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxSize(0.75f)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                val textColor = if (isCurrent) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Unspecified
                }
                Text(
                    text = song.title,
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
                    showEnqueueOption = false
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.remove_from_queue)) },
                        onClick = onRemoveFromQueue
                    )
                }
            }
        }
    }
}