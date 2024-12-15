package com.example.finalsproject.ui.screen

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.finalsproject.R
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.Playlist
import com.example.finalsproject.model.Song
import com.example.finalsproject.model.apiResponse.ShazamResponse
import com.example.finalsproject.ui.composable.PlaylistCard
import com.example.finalsproject.ui.composable.SongCard
import com.example.finalsproject.ui.viewmodel.MusicQueueState
import com.example.finalsproject.ui.viewmodel.MusicQueueViewModel
import com.example.finalsproject.ui.viewmodel.SearchScreenState
import com.example.finalsproject.ui.viewmodel.SearchScreenViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    vm: SearchScreenViewModel,
    musicQueueVm: MusicQueueViewModel,
    navBack: () -> Unit,
    navToPlaylist: (Long) -> Unit,
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val musicQueueState by musicQueueVm.state.collectAsStateWithLifecycle()

    var showMenu by remember { mutableStateOf(false) }
    var showSpeechToTextDialog by remember { mutableStateOf(false) }
    var showSongRecognitionDialog by remember { mutableStateOf(false) }
    var isActive by remember { mutableStateOf(true) }

    Scaffold { innerPadding ->
        SearchBar(
            query = state.searchField,
            onQueryChange = vm::onSearchFieldChange,
            onSearch = vm::onSearchFieldChange,
            active = isActive,
            onActiveChange = { isActive = it },
            placeholder = { Text("Search for song, artist,...") },
            leadingIcon = {
                IconButton(onClick = navBack) {
                    Icon(
                        imageVector = Icons.Rounded.ChevronLeft,
                        contentDescription = stringResource(R.string.navigate_back)
                    )
                }
            },
            trailingIcon = {
                Row {
                    if (state.searchField.isNotEmpty()) {
                        IconButton(onClick = { vm.onSearchFieldChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.clear_text_field)
                            )
                        }
                    }
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = stringResource(R.string.options)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Mic,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            text = { Text(text = stringResource(R.string.search_by_voice)) },
                            onClick = {
                                showSpeechToTextDialog = true
                                showMenu = false
                            }
                        )

                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.MusicNote,
                                    contentDescription = "Melody",
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            text = { Text(text = stringResource(R.string.find_a_melody)) },
                            onClick = {
                                showSongRecognitionDialog = true
                                showMenu = false
                            }
                        )
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            windowInsets = WindowInsets.systemBars,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                SearchModeRow(
                    currentSearchMode = state.searchMode,
                    onSelectMode = vm::onSearchModeChange
                )
                if (state.searchMode == SearchScreenState.SearchMode.BY_TITLE)
                    when (val status = state.playlistsResult) {
                        FetchStatus.Failed -> {
                            // TODO
                        }

                        FetchStatus.Idle -> {}
                        FetchStatus.Loading -> {
                            // TODO
                        }

                        is FetchStatus.Ready -> {
                            SectionTitle(title = stringResource(R.string.playlists))
                            PlaylistList(
                                playlists = status.data,
                                onClick = navToPlaylist,
                                onLoadMore = vm::onLoadMorePlaylistSearch
                            )
                        }
                    }
                when (val status = state.songsResult) {
                    FetchStatus.Failed -> {
                        // TODO
                    }

                    FetchStatus.Idle -> {}

                    FetchStatus.Loading -> {
                        // TODO
                    }

                    is FetchStatus.Ready -> {
                        SectionTitle(
                            title = state.songsResultMessage
                                ?: stringResource(R.string.songs).replaceFirstChar { it.uppercase() }
                        )
                        SongList(
                            songs = status.data,
                            onLoadMore = vm::onLoadMoreSearch,
                        )
                    }
                }
            }
        }

        if (showSpeechToTextDialog) {
            val context = LocalContext.current
            val onStart: () -> Unit = {
                if (musicQueueState.status.let {
                        it is MusicQueueState.Status.Ready && it.isPlaying
                    }
                ) {
                    musicQueueVm.togglePlayOrPause()
                }
                vm.startVoiceSearch(context)
            }
            LaunchedEffect(showSpeechToTextDialog) {
                onStart()
            }
            VoiceSearchDialog(
                status = state.voiceSearchResult,
                onStartVoiceSearch = onStart,
                onDismissRequest = {
                    showSpeechToTextDialog = false
                    state.voiceSearchResult.let {
                        if (it is FetchStatus.Ready) {
                            vm.onSearchFieldChange(it.data)
                        }
                        vm.clearVoiceSearchStatus()
                    }
                }
            )
        }

        if (showSongRecognitionDialog) {
            val context = LocalContext.current
            val onClickRecord: () -> Unit = {
                musicQueueState.status.let {
                    if (it is MusicQueueState.Status.Ready && it.isPlaying) {
                        musicQueueVm.togglePlayOrPause()
                    }
                }
                vm.startSongRecognition(context)
            }
            LaunchedEffect(Unit) {
                onClickRecord()
            }
            SongRecognitionDialog(
                shazamResult = state.songRecognitionResult,
                internalSearchResult = state.songRecognitionInternalResult,
                isRecording = state.isMicRecording,
                onClickRecord = onClickRecord,
                onDismissRequest = {
                    if (
                        !state.isMicRecording &&
                        state.songRecognitionResult !is FetchStatus.Loading
                    ) {
                        vm.clearSongRecognitionResult()
                        showSongRecognitionDialog = false
                    }
                }
            )
        }
    }
}

@Composable
private fun SearchModeRow(
    currentSearchMode: SearchScreenState.SearchMode,
    onSelectMode: (SearchScreenState.SearchMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.horizontalScroll(rememberScrollState())
    ) {
        SearchScreenState.SearchMode.BY_TITLE.let {
            SearchModeButton(
                name = stringResource(R.string.by_title),
                isSelected = it == currentSearchMode,
                onClick = { onSelectMode(it) },
            )
        }
        SearchScreenState.SearchMode.BY_ARTIST.let {
            SearchModeButton(
                name = stringResource(R.string.by_artist),
                isSelected = it == currentSearchMode,
                onClick = { onSelectMode(it) },
            )
        }
        SearchScreenState.SearchMode.BY_EMOTION.let {
            SearchModeButton(
                name = stringResource(R.string.by_emotion),
                isSelected = it == currentSearchMode,
                onClick = { onSelectMode(it) },
            )
        }
    }
}

@Composable
private fun SearchModeButton(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = isSelected,
        transitionSpec = { fadeIn() togetherWith ExitTransition.None },
        label = "searchModeButton"
    ) { state ->
        if (state) {
            Button(onClick = {}, modifier = modifier) {
                Text(text = name)
            }
        } else {
            OutlinedButton(onClick = onClick) {
                Text(text = name)
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
private fun PlaylistList(
    playlists: List<Playlist>,
    onLoadMore: () -> Unit,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberLazyListState()
    var hasTriggeredLoad by remember { mutableStateOf(false) }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(end = 80.dp),
        state = scrollState,
        modifier = modifier
    ) {
        items(items = playlists) { playlist ->
            PlaylistCard(playlist = playlist, onClick = { onClick(playlist.id) })
        }
    }
    val canScrollForward by remember { derivedStateOf { scrollState.canScrollForward } }
    LaunchedEffect(canScrollForward) {
        if (hasTriggeredLoad) {
            return@LaunchedEffect
        }
        if (canScrollForward) {
            return@LaunchedEffect
        }
        hasTriggeredLoad = true
        onLoadMore()
        delay(200L)
        hasTriggeredLoad = false
    }
}

@Composable
private fun SongList(
    songs: List<Song>,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberLazyListState()
    var hasTriggeredLoad by remember { mutableStateOf(false) }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        state = scrollState,
        modifier = modifier.heightIn(min = 1.dp, max = 1500.dp)
    ) {
        items(items = songs) { song ->
            SongCard(song = song)
        }
    }
    val canScrollForward by remember { derivedStateOf { scrollState.canScrollForward } }
    LaunchedEffect(canScrollForward) {
        if (hasTriggeredLoad) {
            return@LaunchedEffect
        }
        if (canScrollForward) {
            return@LaunchedEffect
        }
        hasTriggeredLoad = true
        onLoadMore()
        delay(200L)
        hasTriggeredLoad = false
    }
}

@Composable
private fun VoiceSearchDialog(
    status: FetchStatus<String>,
    onStartVoiceSearch: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        ElevatedCard(
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = stringResource(R.string.search_by_voice))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (status is FetchStatus.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(80.dp))
                        }
                        IconButton(
                            onClick = onStartVoiceSearch,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                        ) {
                            Icon(
                                imageVector = if (status is FetchStatus.Loading) {
                                    Icons.Rounded.Stop
                                } else {
                                    Icons.Rounded.Mic
                                },
                                contentDescription = stringResource(R.string.search_by_voice),
                            )
                        }
                    }

                    when (status) {
                        FetchStatus.Failed -> {
                            /*TODO*/
                        }

                        FetchStatus.Idle, FetchStatus.Loading -> {}

                        is FetchStatus.Ready -> Text(status.data)
                    }
                }
                if (status is FetchStatus.Ready) {
                    Button(onClick = onDismissRequest, modifier = Modifier.align(Alignment.End)) {
                        Text(text = stringResource(R.string.search))
                    }
                }
            }
        }
    }
}

@Composable
private fun SongRecognitionDialog(
    shazamResult: FetchStatus<ShazamResponse.Recognize>,
    internalSearchResult: FetchStatus<List<Song>>,
    isRecording: Boolean,
    onClickRecord: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        ElevatedCard(
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                SongRecognitionControls(
                    isRecording = isRecording,
                    isLoading = shazamResult is FetchStatus.Loading || isRecording,
                    onClickRecord = onClickRecord,
                )
                when (shazamResult) {
                    is FetchStatus.Ready -> {
                        Log.d("TEST", "$shazamResult")
                        shazamResult.data.track.let { track ->
                            if (track != null) {
                                SongRecognitionMatchDisplay(
                                    title = track.title,
                                    artist = track.artist,
                                    coverArtUrl = track.images?.coverArt
                                )
                                HorizontalDivider()
                                when (internalSearchResult) {
                                    is FetchStatus.Ready ->
                                        if (internalSearchResult.data.isNotEmpty()) {
                                            SectionTitle(
                                                title = stringResource(R.string.songs_matches),
                                                modifier = Modifier.align(Alignment.Start)
                                            )
                                            SongList(
                                                songs = internalSearchResult.data,
                                                onLoadMore = {},
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }

                                    FetchStatus.Failed -> {
                                        // TODO
                                    }

                                    FetchStatus.Idle, FetchStatus.Loading -> {}
                                }
                                track.hub?.options?.first()?.actions?.first()?.uri?.let { uri ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.align(Alignment.Start)
                                    ) {
                                        SectionTitle(
                                            title = stringResource(R.string.listen_on_apple_music)
                                        )
                                        LocalUriHandler.current.let { uriHandler ->
                                            Button(onClick = { uriHandler.openUri(uri) }) {
                                                Text(text = stringResource(R.string.open))
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text(text = "No match was found")
                            }
                        }
                    }

                    FetchStatus.Failed -> {
                        // TODO
                    }

                    FetchStatus.Idle, FetchStatus.Loading -> {}
                }
            }
        }
    }
}

@Composable
private fun SongRecognitionControls(
    isRecording: Boolean,
    isLoading: Boolean,
    onClickRecord: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.find_a_melody),
            style = MaterialTheme.typography.titleLarge
        )
        Box(contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator(
                    strokeWidth = 8.dp,
                    modifier = Modifier.size(80.dp)
                )
            }
            IconButton(
                onClick = onClickRecord,
                enabled = !isLoading,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1A5AFF),
                                Color(0xFF0197FF)
                            )
                        )
                    )
            ) {
                Icon(
                    imageVector = if (isRecording) {
                        Icons.Rounded.Stop
                    } else {
                        Icons.Rounded.MusicNote
                    },
                    contentDescription = "Melody Icon",
                    tint = Color.White,
                    modifier = Modifier.size(52.dp)
                )
            }
        }
    }
}

@Composable
private fun SongRecognitionMatchDisplay(
    title: String,
    artist: String,
    coverArtUrl: String?,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        if (coverArtUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(coverArtUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
            )
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = artist,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}