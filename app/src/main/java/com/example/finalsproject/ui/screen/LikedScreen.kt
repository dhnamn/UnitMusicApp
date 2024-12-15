package com.example.finalsproject.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finalsproject.R
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.Song
import com.example.finalsproject.ui.composable.SongCard
import com.example.finalsproject.ui.viewmodel.LikedScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedScreen(
    viewModel: LikedScreenViewModel,
    navBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
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
                    Text(text = stringResource(R.string.favorites))
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        when (val songs = state.songs) {
            is FetchStatus.Ready -> {
                SongList(
                    songs = songs.data,
                    isLoading = state.isLoading,
                    onLoadMore = viewModel::loadMore,
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(8.dp)
                )
            }

            else -> {}
        }
    }
}

@Composable
private fun SongList(
    songs: List<Song>,
    isLoading: Boolean,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberLazyListState()
    val canScrollForward by remember { derivedStateOf { scrollState.canScrollForward } }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = scrollState,
        modifier = modifier.heightIn(max = 1500.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(songs) {
            SongCard(song = it)
        }
    }

    LaunchedEffect(canScrollForward) {
        if (canScrollForward || isLoading) {
            return@LaunchedEffect
        }
        onLoadMore()
    }
}