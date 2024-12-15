@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.finalsproject.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finalsproject.R
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.Playlist
import com.example.finalsproject.model.Song
import com.example.finalsproject.ui.composable.SongCard
import com.example.finalsproject.ui.viewmodel.MusicQueueViewModel
import com.example.finalsproject.ui.viewmodel.PlaylistScreenViewModel
import com.example.finalsproject.utils.Utils
import com.example.finalsproject.utils.base64ToImageBitmap
import com.example.finalsproject.utils.samplePlaylist
import com.example.finalsproject.utils.secondsToTimeFormat

@Composable
fun PlaylistScreen(
    viewModel: PlaylistScreenViewModel,
    musicQueueViewModel: MusicQueueViewModel,
    navBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopBar(onClickBack = navBack, modifier = Modifier.statusBarsPadding())
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            when (val playlistContent = state.playlistContent) {
                is FetchStatus.Ready -> {
                    val backgroundBitmap: ImageBitmap by remember {
                        mutableStateOf(
                            Utils.base64ToImageBitmap(playlistContent.data.imgBase64)
                        )
                    }
                    var isInfoExpanded by remember { mutableStateOf(true) }

                    BackgroundImage(bitmap = backgroundBitmap)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth()
                            .pointerInput(isInfoExpanded) {
                                if (!isInfoExpanded) {
                                    return@pointerInput
                                }
                                detectVerticalDragGestures { _, dragAmount ->
                                    if (dragAmount < 0) {
                                        isInfoExpanded = false
                                    }
                                }
                            }
                    ) {
                        PlaylistInfoSection(
                            playlist = playlistContent.data,
                            isExpanded = isInfoExpanded,
                            onClickPlay = {
                                musicQueueViewModel.setQueue(playlistContent.data.songs!!)
                            },
                            modifier = Modifier
                                .pointerInput(isInfoExpanded) {
                                    if (isInfoExpanded) {
                                        return@pointerInput
                                    }
                                    detectVerticalDragGestures { _, dragAmount ->
                                        if (dragAmount > 0) {
                                            isInfoExpanded = true
                                        }
                                    }
                                }
                        )
                        SongList(
                            songs = playlistContent.data.songs!!,
                            scrollable = !isInfoExpanded,
                        )
                    }
                }

                FetchStatus.Loading, FetchStatus.Idle -> ContentLoading()

                FetchStatus.Failed -> ContentRetry(onClick = viewModel::onClickRetryGetPlaylistTask)
            }

        }

    }
}

@Composable
private fun TopBar(
    onClickBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = onClickBack,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .align(Alignment.CenterStart)
            ) {
                Icon(imageVector = Icons.Rounded.ChevronLeft, contentDescription = null)
            }
            Text(
                text = stringResource(R.string.playlist),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun BackgroundImage(
    bitmap: ImageBitmap,
    modifier: Modifier = Modifier
) {
    Image(
        bitmap = bitmap,
        contentDescription = null,
        contentScale = ContentScale.FillHeight,
        colorFilter = ColorFilter.tint(
            color = Color(1f, 1f, 1f, 0.5f),
            blendMode = BlendMode.DstAtop
        ),
        modifier = modifier
            .fillMaxHeight()
            .blur(12.dp)
    )
}

@Composable
private fun PlaylistInfoSection(
    playlist: Playlist,
    isExpanded: Boolean,
    onClickPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    SharedTransitionLayout(modifier = modifier.fillMaxWidth()) {
        AnimatedContent(isExpanded, label = "PlaylistInfoSection") { targetState ->
            when (targetState) {
                true -> {
                    PlaylistInfoSectionExpanded(
                        playlist = playlist,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@AnimatedContent,
                        onClickPlay = onClickPlay
                    )
                }

                false -> {
                    PlaylistInfoSectionCollapsed(
                        playlist = playlist,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@AnimatedContent,
                        onClickPlay = onClickPlay
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaylistInfoSectionExpanded(
    playlist: Playlist,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClickPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        PlaylistImage(
            imgBase64 = playlist.imgBase64,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = Modifier.size(300.dp)
        )
        PlaylistTitle(
            title = playlist.title,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
        )
        PlaylistDescription(
            description = playlist.description,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
        )
        val durationInSeconds by remember {
            mutableIntStateOf(playlist.songs!!.sumOf { it.length })
        }
        CountAndDurationText(
            count = playlist.songs!!.size,
            durationInSeconds = durationInSeconds,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
        )
        DividerAndPlay(
            onClickPlay = onClickPlay,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
        )
    }
}

@Composable
private fun PlaylistInfoSectionCollapsed(
    playlist: Playlist,
    onClickPlay: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlaylistImage(
                imgBase64 = playlist.imgBase64,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.size(120.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PlaylistTitle(
                    title = playlist.title,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
                val durationInSeconds by remember {
                    mutableIntStateOf(playlist.songs!!.sumOf { it.length })
                }
                CountAndDurationText(
                    count = playlist.songs!!.size,
                    durationInSeconds = durationInSeconds,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }
        DividerAndPlay(
            onClickPlay = onClickPlay,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
        )
    }
}

@Composable
private fun PlaylistImage(
    imgBase64: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    with(sharedTransitionScope) {
        ElevatedCard(
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
            modifier = modifier
        ) {
            val bitmap by remember {
                mutableStateOf(Utils.base64ToImageBitmap(imgBase64))
            }
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState("image"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

@Composable
private fun PlaylistTitle(
    title: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    with(sharedTransitionScope) {
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
            modifier = modifier.sharedElement(
                state = rememberSharedContentState("title"),
                animatedVisibilityScope = animatedVisibilityScope
            )
        )
    }
}

@Composable
private fun PlaylistDescription(
    description: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    with(sharedTransitionScope) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge.copy(
                shadow = Shadow(
                    color = Color(0x77000000),
                    offset = Offset(4f, 4f),
                    blurRadius = 2f
                )
            ),
            modifier = modifier.sharedElement(
                state = rememberSharedContentState("description"),
                animatedVisibilityScope = animatedVisibilityScope
            )
        )
    }
}

@Composable
private fun CountAndDurationText(
    count: Int,
    durationInSeconds: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    with(sharedTransitionScope) {
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
            modifier = modifier.sharedElement(
                state = rememberSharedContentState("countAndDuration"),
                animatedVisibilityScope = animatedVisibilityScope
            )
        )
    }
}

@Composable
private fun DividerAndPlay(
    onClickPlay: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    with(sharedTransitionScope) {
        Box(
            modifier = modifier
                .sharedElement(
                    state = rememberSharedContentState("dividerAndPlay"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .fillMaxWidth()
        ) {
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
}

@Composable
private fun SongList(
    songs: List<Song>,
    scrollable: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        userScrollEnabled = scrollable,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        items(items = songs, key = { it.id }) {
            SongCard(song = it)
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


@Composable
private fun ContentRetry(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = stringResource(R.string.failed_to_fetch_content))
            Button(onClick = onClick) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}

@Preview
@Composable
private fun TopBarAndBackgroundPreview() {
    Scaffold(
        topBar = { TopBar(onClickBack = {}, modifier = Modifier.statusBarsPadding()) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
        BackgroundImage(bitmap = Utils.base64ToImageBitmap(Utils.samplePlaylist.imgBase64))
    }
}

@Preview
@Composable
private fun PlaylistInfoSectionPreview() {
    var isExpanded by remember { mutableStateOf(true) }
    Scaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectVerticalDragGestures { _, dragAmount ->
                isExpanded = if (dragAmount < 0) {
                    false
                } else {
                    true
                }
            }
        }
    ) { innerPadding ->
        PlaylistInfoSection(
            playlist = Utils.samplePlaylist,
            isExpanded = isExpanded,
            onClickPlay = {},
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = 20.dp)
        )
    }
}