package com.example.finalsproject.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalsproject.R
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.Song
import com.example.finalsproject.ui.composable.SongCard
import com.example.finalsproject.ui.viewmodel.LeaderboardViewModel
import com.example.finalsproject.ui.viewmodel.MusicQueueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel,
    musicQueueViewModel: MusicQueueViewModel,
    navBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.leaderboard),
                        fontSize = 24.sp,
                        fontWeight = MaterialTheme.typography.titleLarge.fontWeight
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navBack) {
                        Icon(
                            imageVector = Icons.Rounded.ChevronLeft,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            state.topSongs.let {
                                if (it is FetchStatus.Ready) {
                                    musicQueueViewModel.setQueue(it.data)
                                }
                            }
                        },
                        enabled = state.topSongs is FetchStatus.Ready,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Play all", fontSize = 16.sp, color = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (val topSongs = state.topSongs) {
                is FetchStatus.Idle, FetchStatus.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                is FetchStatus.Ready -> {
                    LeaderboardContent(
                        songs = topSongs.data,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                is FetchStatus.Failed -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Failed to load leaderboard songs.",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = viewModel::onClickRetryGetTopSongs) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardContent(
    songs: List<Song>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(8.dp)
    ) {
        items(items = songs, key = { it.id }) { song ->
            SongCard(song = song)
        }
    }
}
