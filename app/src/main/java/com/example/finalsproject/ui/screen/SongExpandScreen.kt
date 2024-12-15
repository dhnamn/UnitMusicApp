package com.example.finalsproject.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.MusicOff
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finalsproject.R
import com.example.finalsproject.model.Song
import com.example.finalsproject.ui.navhost.CustomTransition
import com.example.finalsproject.ui.viewmodel.MusicQueueState
import com.example.finalsproject.ui.viewmodel.MusicQueueViewModel
import com.example.finalsproject.utils.Utils
import com.example.finalsproject.utils.base64ToImageBitmap
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

private const val SCREEN_MAIN = "main"
private const val SCREEN_MORE = "more"

@Composable
fun SongExpandScreen(
    viewModel: MusicQueueViewModel,
    navBack: () -> Unit,
    navToQueueManager: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        state.currentSong?.also {
            val bitmap: ImageBitmap by remember {
                mutableStateOf(Utils.base64ToImageBitmap(it.albumImgBase64))
            }
            Background(bitmap = bitmap)
        }

        NavHost(
            navController = navController,
            startDestination = SCREEN_MAIN
        ) {
            composable(
                route = SCREEN_MAIN,
                enterTransition = { CustomTransition.Enter.slideFromTop },
                exitTransition = { CustomTransition.Exit.slideToTop }
            ) {
                MainScreen(
                    viewModel = viewModel,
                    navBack = navBack,
                    navToQueueManager = navToQueueManager,
                    navToMore = {
                        navController.navigate(SCREEN_MORE) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(
                route = SCREEN_MORE,
                enterTransition = { CustomTransition.Enter.slideFromBottom },
                exitTransition = { CustomTransition.Exit.slideToBottom }
            ) {
                MoreScreen(
                    viewModel = viewModel,
                    navBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun Background(bitmap: ImageBitmap, modifier: Modifier = Modifier) {
    AnimatedContent(
        targetState = bitmap,
        label = "background",
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) { target ->
        Image(
            bitmap = target,
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
}

@Composable
private fun MainScreen(
    viewModel: MusicQueueViewModel,
    navBack: () -> Unit,
    navToQueueManager: () -> Unit,
    navToMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopBar(
                queueSize = state.currentQueue.size,
                navBack = navBack,
                navToQueueManager = navToQueueManager,
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier)
            if (state.currentIdx == -1) {
                TrackEmpty(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                TrackDisplay(
                    current = state.currentSong!!,
                    prev = state.prevSong!!,
                    next = state.nextSong!!,
                    goToPrev = viewModel::goToPrev,
                    goToNext = viewModel::goToNextExplicitly
                )
            }

            val currentTimeStampMs by viewModel.progressStateMs.collectAsState()
            InfoAndProgressBar(
                song = state.currentSong,
                onClickFavorite = { state.currentSong?.let(viewModel.likeNotifier::toggleLike) },
                currentTimeStampMs = currentTimeStampMs,
                seek = viewModel::seek,
            )

            BottomBar(
                shuffleMode = state.shuffleMode,
                isPlaying = when (val status = state.status) {
                    is MusicQueueState.Status.Ready -> status.isPlaying
                    else -> false
                },
                repeatMode = state.repeatMode,
                onClickShuffle = viewModel::toggleShuffleMode,
                onClickPlayOrPause = viewModel::togglePlayOrPause,
                onClickRepeat = viewModel::cycleRepeatMode,
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = navToMore,
                        enabled = state.currentSong != null
                    )
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = "More",
                    modifier = Modifier.size(60.dp)
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    queueSize: Int,
    navBack: () -> Unit,
    navToQueueManager: () -> Unit,
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

            Text(
                text = stringResource(R.string.x_songs_in_the_queue, queueSize),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.Center)
            )

            IconButton(
                onClick = navToQueueManager,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Rounded.FormatListNumbered,
                    contentDescription = stringResource(R.string.navigate_back)
                )
            }
        }
    }
}

@Composable
private fun BottomBar(
    shuffleMode: Boolean,
    isPlaying: Boolean,
    repeatMode: MusicQueueState.RepeatMode,
    onClickShuffle: () -> Unit,
    onClickPlayOrPause: () -> Unit,
    onClickRepeat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClickShuffle,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Shuffle,
                    tint = if (shuffleMode) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Black
                    },
                    contentDescription = "Shuffle",
                    modifier = Modifier.fillMaxSize()
                )
            }

            IconButton(
                onClick = onClickPlayOrPause,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) {
                        Icons.Rounded.Pause
                    } else {
                        Icons.Rounded.PlayArrow
                    },
                    contentDescription = "Pause",
                    modifier = Modifier.fillMaxSize()
                )
            }

            IconButton(
                onClick = onClickRepeat,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = if (repeatMode == MusicQueueState.RepeatMode.ONE) {
                        Icons.Rounded.RepeatOne
                    } else {
                        Icons.Rounded.Repeat
                    },
                    tint = if (repeatMode == MusicQueueState.RepeatMode.OFF) {
                        Color.Black
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    contentDescription = "Repeat",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun InfoAndProgressBar(
    song: Song?,
    onClickFavorite: () -> Unit,
    currentTimeStampMs: Float,
    seek: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        if (song != null) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(0.85f)) {
                    Text(
                        text = song.title,
                        maxLines = 1,
                        style = LocalTextStyle.current.copy(
                            shadow = Shadow(
                                color = Color(0x66000000),
                                offset = Offset(3f, 3f)
                            )
                        ),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .basicMarquee()
                    )

                    Text(
                        text = song.artist,
                        maxLines = 1,
                        style = LocalTextStyle.current.copy(
                            shadow = Shadow(
                                color = Color(0x66000000),
                                offset = Offset(2f, 2f)
                            )
                        ),
                        fontSize = 18.sp,
                        modifier = Modifier.basicMarquee()
                    )
                }
                IconButton(onClick = onClickFavorite, modifier = Modifier.weight(0.15f)) {
                    val mod = Modifier.size(64.dp)
                    if (song.likedByUser) {
                        Icon(
                            imageVector = Icons.Rounded.Favorite,
                            contentDescription = stringResource(R.string.add_to_favorites),
                            tint = Color(0xFFFF0044),
                            modifier = mod
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.FavoriteBorder,
                            contentDescription = stringResource(R.string.add_to_favorites),
                            modifier = mod
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime((currentTimeStampMs / 1000).toInt()),
                fontSize = 16.sp,
                textAlign = TextAlign.Start
            )
            Text(
                text = formatTime(song?.length ?: 0),
                fontSize = 16.sp,
                textAlign = TextAlign.End
            )
        }

        var isHoldingSlider by remember { mutableStateOf(false) }
        var desiredDurationMs by remember { mutableFloatStateOf(0f) }
        var songLengthMs by remember { mutableFloatStateOf(0f) }
        LaunchedEffect(song?.id) {
            songLengthMs = if (song != null) {
                song.length.toFloat() * 1000f
            } else 0f
        }
        Slider(
            value = if (isHoldingSlider) desiredDurationMs else currentTimeStampMs,
            onValueChange = {
                isHoldingSlider = true
                desiredDurationMs = it
            },
            onValueChangeFinished = {
                seek(desiredDurationMs)
                isHoldingSlider = false
            },
            valueRange = 0f..songLengthMs,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun formatTime(secs: Int): String {
    return "${"%02d".format(secs / 60)}:${"%02d".format(secs % 60)}"
}

@Composable
private fun TrackDisplay(
    current: Song,
    prev: Song,
    next: Song,
    goToPrev: () -> Unit,
    goToNext: () -> Unit
) {
    var currentCover: ImageBitmap by remember {
        mutableStateOf(Utils.base64ToImageBitmap(current.albumImgBase64))
    }
    var prevCover: ImageBitmap? by remember { mutableStateOf(null) }
    var nextCover: ImageBitmap? by remember { mutableStateOf(null) }
    var prevId: Long by remember { mutableLongStateOf(-1L) }
    var nextId: Long by remember { mutableLongStateOf(-1L) }
    LaunchedEffect(prev) {
        if (prevCover == null || prevId != prev.id) {
            prevCover = Utils.base64ToImageBitmap(prev.albumImgBase64)
            prevId = prev.id
        }
    }
    LaunchedEffect(next) {
        if (nextCover == null || nextId != next.id) {
            nextCover = Utils.base64ToImageBitmap(next.albumImgBase64)
            nextId = next.id
        }
    }
    LaunchedEffect(current) {
        currentCover = Utils.base64ToImageBitmap(current.albumImgBase64)
    }
    SideEffect {
        if (prevCover == null) {
            prevCover = currentCover
        }
        if (nextCover == null) {
            nextCover = currentCover
        }
    }
    val threshold = 80f
    val maxSize = 320f
    val minSize = 200f
    val diff = maxSize - minSize
    var totalDragAmount by remember { mutableFloatStateOf(0f) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(maxSize.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (totalDragAmount < -threshold) {
                            goToNext()
                            prevCover = currentCover
                            currentCover = nextCover!!
                            nextCover = null
                        } else if (totalDragAmount > threshold) {
                            goToPrev()
                            nextCover = currentCover
                            currentCover = prevCover!!
                            prevCover = null
                        }
                        totalDragAmount = 0f
                    }
                ) { _, dragAmount -> totalDragAmount += dragAmount }
            }
    ) {
        val currentSize = maxSize - totalDragAmount.absoluteValue / threshold * diff / 2f
        val prevSize = minSize + min(diff, totalDragAmount / threshold * diff / 2)
        val nextSize = minSize - max(-diff, totalDragAmount / threshold * diff / 2)

        prevCover?.also {
            ElevatedCard(
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(prevSize.dp)
                    .aspectRatio(1f)
                    .offset { IntOffset(totalDragAmount.toInt() / 2, 0) }
                    .zIndex(prevSize)
            ) {
                Image(
                    bitmap = it,
                    contentDescription = "Previous Track",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        nextCover?.also {
            ElevatedCard(
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(nextSize.dp)
                    .aspectRatio(1f)
                    .offset { IntOffset(totalDragAmount.toInt() / 2, 0) }
                    .zIndex(nextSize)
            ) {
                Image(
                    bitmap = it,
                    contentDescription = "Next Track",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        ElevatedCard(
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .align(Alignment.Center)
                .size(currentSize.dp)
                .aspectRatio(1f)
                .offset { IntOffset(totalDragAmount.toInt() * 3 / 2, 0) }
                .zIndex(currentSize)
        ) {
            Image(
                bitmap = currentCover,
                contentDescription = "Current Track",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun TrackEmpty(modifier: Modifier = Modifier) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.elevatedCardColors().copy(containerColor = Color.Gray),
        modifier = modifier
    ) {
        Box(modifier = Modifier.size(300.dp)) {
            Icon(
                imageVector = Icons.Rounded.MusicOff,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(0.3f)
            )
        }
    }
}

private fun formatNumber(value: Long): String {
    return NumberFormat.getNumberInstance(Locale.US).format(value)
}

@Composable
private fun MoreScreen(
    viewModel: MusicQueueViewModel,
    navBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        containerColor = Color.Transparent,
        modifier = modifier
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = navBack)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowDropUp,
                    contentDescription = stringResource(R.string.navigate_back),
                    modifier = Modifier.size(60.dp)
                )
            }
            MoreInfo(song = state.currentSong!!)
            CreateStoryCard(viewModel = viewModel)
        }
    }
}

@Composable
private fun MoreInfo(
    song: Song,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            MoreInfoRow(label = "Title:", value = song.title)
            MoreInfoRow(label = "Artist:", value = song.artist)
            MoreInfoRow(label = "Album:", value = song.album)
            MoreInfoRow(label = "Genre:", value = song.genre)
            MoreInfoRow(label = "Duration:", value = formatTime(song.length))
            MoreInfoRow(label = "Likes:", value = formatNumber(song.likeCount))
            MoreInfoRow(label = "Plays:", value = formatNumber(song.playCount))
        }
    }
}

@Composable
private fun MoreInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
private fun CreateStoryCard(
    viewModel: MusicQueueViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    Box(
        modifier = modifier
            .width(424.dp)
            .wrapContentHeight()
//            .heightIn(min = 160.dp, max = 260.dp)
//            .height(240.dp)
            .clip(shape = RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    0f to Color(0xff79c4ff),
                    1f to Color(0xffe6abff),
                    start = Offset(330.5f, 42f),
                    end = Offset(111f, 140f)
                )
            )
            .border(
                border = BorderStroke(3.dp, Color(0xff7446ff)),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.generate_story_from_the_song),
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { viewModel.getStory() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .defaultMinSize(minHeight = 48.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.gpt_logo),
                        contentDescription = "GPT Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Let's go",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xff7446ff),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            if (state.isLoadingStory) {
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator(modifier = Modifier.size(28.dp))
            }
            val scrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 180.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = state.story,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}