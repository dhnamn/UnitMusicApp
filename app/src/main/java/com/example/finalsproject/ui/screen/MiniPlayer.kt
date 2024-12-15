package com.example.finalsproject.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalsproject.R
import com.example.finalsproject.ui.viewmodel.MusicQueueState
import com.example.finalsproject.ui.viewmodel.MusicQueueViewModel
import com.example.finalsproject.utils.Utils
import com.example.finalsproject.utils.base64ToImageBitmap
import com.example.finalsproject.utils.samplePlaylist

@Composable
fun MiniPlayer(
    viewModel: MusicQueueViewModel,
    navToSongExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    ElevatedCard(
        onClick = navToSongExpand,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            state.currentSong?.also { song ->
                AnimatedContent(
                    targetState = song,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "background"
                ) { target ->
                    Image(
                        bitmap = Utils.base64ToImageBitmap(target.albumImgBase64),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        colorFilter = ColorFilter.tint(
                            color = Color(1f, 1f, 1f, 0.5f),
                            blendMode = BlendMode.DstAtop
                        ),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    state.currentSong?.also {
                        SongInfo(title = it.title, artist = it.artist)
                    }
                }
                ControlButtons(
                    isPlaying = when (val status = state.status) {
                        is MusicQueueState.Status.Ready -> status.isPlaying
                        else -> false
                    },
                    goToPrev = viewModel::goToPrev,
                    togglePlayOrPause = viewModel::togglePlayOrPause,
                    goToNextExplicitly = viewModel::goToNextExplicitly
                )
            }
            val currentTimeStampMs by viewModel.progressStateMs.collectAsState()
            LinearProgressIndicator(
                progress = {
                    when (val song = state.currentSong) {
                        null -> 0f
                        else -> currentTimeStampMs / (song.length * 1000f)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SongInfo(
    title: String,
    artist: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.basicMarquee()
        )
        Text(
            text = artist,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            modifier = Modifier.basicMarquee()
        )
    }
}

@Composable
private fun ControlButtons(
    isPlaying: Boolean,
    goToPrev: () -> Unit,
    togglePlayOrPause: () -> Unit,
    goToNextExplicitly: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(onClick = goToPrev) {
            Icon(
                imageVector = Icons.Rounded.SkipPrevious,
                contentDescription = stringResource(R.string.go_to_previous_song)
            )
        }
        IconButton(onClick = togglePlayOrPause) {
            Icon(
                imageVector = if (isPlaying) {
                    Icons.Rounded.Pause
                } else {
                    Icons.Rounded.PlayArrow
                },
                contentDescription = stringResource(R.string.play_or_pause)
            )
        }
        IconButton(onClick = goToNextExplicitly) {
            Icon(
                imageVector = Icons.Rounded.SkipNext,
                contentDescription = stringResource(R.string.go_to_next_song)
            )
        }
    }
}

@Preview
@Composable
private fun MiniPlayPreview() {
    Scaffold(
        bottomBar = {
            MiniPlayer(
                viewModel = viewModel<MusicQueueViewModel>(factory = MusicQueueViewModel.Factory)
                    .apply { setQueue(Utils.samplePlaylist.songs!!) },
                navToSongExpand = {},
                modifier = Modifier.systemBarsPadding()
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
    }
}
