package com.example.finalsproject.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finalsproject.R
import com.example.finalsproject.model.FetchStatus
import com.example.finalsproject.model.UserPlaylist
import com.example.finalsproject.ui.viewmodel.UserPlaylistListViewModel

@Composable
fun UserPlaylistListScreen(
    viewModel: UserPlaylistListViewModel,
    navBack: () -> Unit,
    navToPlaylist: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopBar(navBack = navBack, modifier = Modifier.statusBarsPadding())
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            when (val userPlaylists = state.userPlaylists) {
                is FetchStatus.Ready -> {
                    items(items = userPlaylists.data, key = { it.id }) {
                        UserPlaylistCard(
                            playlist = it,
                            onClick = { navToPlaylist(it.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                else -> {
                    items(10) {
                        UserPlaylistLoadingCard(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(navBack: () -> Unit, modifier: Modifier = Modifier) {
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
            Text(text = stringResource(R.string.my_playlists))
        },
        modifier = modifier
    )
}

@Composable
private fun UserPlaylistCard(
    playlist: UserPlaylist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = playlist.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = playlist.description.ifEmpty { stringResource(R.string.no_description) },
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun UserPlaylistLoadingCard(modifier: Modifier = Modifier) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        modifier = modifier
    ) {
        Box(
            Modifier
                .size(200.dp, 20.dp)
                .clip(CircleShape)
                .background(Color.Black)
        )
        Box(
            Modifier
                .size(300.dp, 20.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )
    }
}