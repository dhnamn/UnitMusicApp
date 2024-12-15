package com.example.finalsproject.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Headset
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.finalsproject.R
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.Playlist
import com.example.finalsproject.model.Song
import com.example.finalsproject.ui.LocalSongViewModel
import com.example.finalsproject.ui.composable.PlaylistCard
import com.example.finalsproject.ui.composable.SongCard
import com.example.finalsproject.ui.composable.SongOptionDropdown
import com.example.finalsproject.ui.viewmodel.HomeScreenViewModel
import com.example.finalsproject.utils.Utils
import com.example.finalsproject.utils.base64ToImageBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    navToLeaderboard: () -> Unit,
    navToPlaylist: (id: Long) -> Unit,
    navToSearch: () -> Unit,
    navToProfile: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = R.drawable.app_icon,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Text(
                            text = stringResource(R.string.app_name),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = navToSearch) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = navToProfile) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            TopSongSection(
                songs = state.topSongs,
                navToLeaderboard = navToLeaderboard,
                onClickRetry = viewModel::onClickRetryGetTopSongs,
            )

            PlaylistSection(
                playlists = state.playlists,
                onClickPlaylist = { navToPlaylist(it) },
                onClickRetry = viewModel::onClickRetryGetPlaylists
            )

            ExploreSection(
                songs = state.exploreSongs,
                onClickRetry = viewModel::onClickRetryGetRandomSongs,
            )
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    retryEnabled: Boolean,
    onClickRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = onClickRetry,
            enabled = retryEnabled
        ) {
            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = stringResource(R.string.retry)
            )
        }
    }
}

@Composable
private fun RetryCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
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

@Composable
fun TopSongSection(
    songs: FetchStatus<List<Song>>,
    navToLeaderboard: () -> Unit,
    onClickRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        SectionTitle(
            title = stringResource(R.string.homescreen_section_top),
            retryEnabled = songs !is FetchStatus.Loading,
            onClickRetry = onClickRetry,
            modifier = Modifier.padding(start = 8.dp)
        )
        when (songs) {
            is FetchStatus.Ready -> {
                val state = rememberLazyListState()
                val currentIdx by remember { derivedStateOf { state.firstVisibleItemIndex } }
                val flingBehavior = rememberSnapFlingBehavior(lazyListState = state)

                LazyRow(
                    state = state,
                    flingBehavior = flingBehavior,
                    contentPadding = PaddingValues(horizontal = 40.dp),
                    horizontalArrangement = Arrangement.spacedBy(48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    items(songs.data.size, key = { idx -> songs.data[idx].id }) { idx ->
                        TopSongCard(
                            song = songs.data[idx],
                            ranking = idx + 1,
                        )
                    }
                }
                IndexIndicator(
                    count = songs.data.size,
                    currentIdx = currentIdx,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Button(
                    onClick = navToLeaderboard,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = stringResource(R.string.view_leaderboard))
                }
            }

            FetchStatus.Loading, FetchStatus.Idle -> {
                TopSongSectionLoading(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            FetchStatus.Failed -> {
                RetryCard(
                    onClick = onClickRetry,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun TopSongCard(
    song: Song,
    ranking: Int,
    modifier: Modifier = Modifier,
) {
    val viewModel = LocalSongViewModel.current
    val containerColor = when (ranking) {
        1 -> Color.Yellow
        2 -> Color.White
        3 -> Color(0xFFDD8F42)
        else -> Color(0xFFCCE5FF)
    }
    ElevatedCard(
        onClick = { viewModel.play(song) },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.elevatedCardColors().copy(containerColor = containerColor),
        modifier = modifier
    ) {
        val width = 300.dp
        Column(
            modifier = Modifier
                .padding(8.dp)
                .width(width)
        ) {
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))) {
                val bitmap by remember {
                    mutableStateOf(Utils.base64ToImageBitmap(song.albumImgBase64))
                }
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier.size(width)
                )
                Card(
                    colors = CardDefaults.elevatedCardColors().copy(
                        containerColor = containerColor
                    ), modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = "#$ranking")
                    }
                }
                Card(
                    colors = CardDefaults.elevatedCardColors().copy(
                        containerColor = containerColor
                    ), modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(imageVector = Icons.Rounded.Headset, contentDescription = null)
                        Text(text = "${song.playCount}")
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = song.artist,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth()
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
                        expanded = showMenu,
                        onDismiss = { showMenu = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopSongSectionLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Gray)
    ) {
        CircularProgressIndicator(
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(144.dp)
                    .height(16.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
            Box(
                modifier = Modifier
                    .width(64.dp)
                    .height(16.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}

@Composable
private fun IndexIndicator(count: Int, currentIdx: Int, modifier: Modifier = Modifier) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        items(count = count) { idx ->
            if (idx == currentIdx) {
                Box(
                    modifier = Modifier
                        .width(16.dp)
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .animateContentSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(shape = CircleShape)
                        .background(Color.Gray)
                        .animateContentSize()
                )
            }
        }
    }
}

@Composable
fun PlaylistSection(
    playlists: FetchStatus<List<Playlist>>,
    onClickPlaylist: (id: Long) -> Unit,
    onClickRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        SectionTitle(
            title = stringResource(R.string.homescreen_section_playlist),
            retryEnabled = playlists !is FetchStatus.Loading,
            onClickRetry = onClickRetry,
        )
        when (playlists) {
            is FetchStatus.Ready -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(playlists.data, key = { it.id }) {
                        PlaylistCard(playlist = it, onClick = { onClickPlaylist(it.id) })
                    }
                }
            }

            FetchStatus.Loading, FetchStatus.Idle -> {
                PlaylistSectionLoading()
            }

            FetchStatus.Failed -> {
                RetryCard(
                    onClick = onClickRetry,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun PlaylistSectionLoading(modifier: Modifier = Modifier) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        userScrollEnabled = false,
        modifier = modifier.fillMaxWidth()
    ) {
        items(3) {
            ElevatedCard(
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.elevatedCardColors().copy(containerColor = Color.Gray),
                modifier = Modifier
                    .width(144.dp)
                    .height(188.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                    )
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(16.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Box(
                        modifier = Modifier
                            .width(72.dp)
                            .height(16.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }
        }
    }
}

@Composable
fun ExploreSection(
    songs: FetchStatus<List<Song>>,
    onClickRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        SectionTitle(
            title = stringResource(R.string.homescreen_section_explore),
            retryEnabled = songs !is FetchStatus.Loading,
            onClickRetry = onClickRetry
        )
        when (songs) {
            is FetchStatus.Ready -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(600.dp)
                ) {
                    items(songs.data, key = { it.id }) {
                        SongCard(song = it)
                    }
                }
            }

            FetchStatus.Loading, FetchStatus.Idle -> {
                ExploreSectionLoading()
            }

            FetchStatus.Failed -> {
                RetryCard(
                    onClick = onClickRetry,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun ExploreSectionLoading(modifier: Modifier = Modifier) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        userScrollEnabled = false,
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        items(3) {
            ElevatedCard(
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.elevatedCardColors().copy(containerColor = Color.Gray),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(16.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Box(
                            modifier = Modifier
                                .width(72.dp)
                                .height(16.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }
            }
        }
    }
}