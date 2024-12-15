package com.example.finalsproject.ui.navhost

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finalsproject.ui.LocalNewPlaylistViewModel
import com.example.finalsproject.ui.LocalSongViewModel
import com.example.finalsproject.ui.screen.AboutScreen
import com.example.finalsproject.ui.screen.HomeScreen
import com.example.finalsproject.ui.screen.LeaderboardScreen
import com.example.finalsproject.ui.screen.LikedScreen
import com.example.finalsproject.ui.screen.MiniPlayer
import com.example.finalsproject.ui.screen.PlaylistScreen
import com.example.finalsproject.ui.screen.ProfileScreen
import com.example.finalsproject.ui.screen.QueueManagerScreen
import com.example.finalsproject.ui.screen.SearchScreen
import com.example.finalsproject.ui.screen.SongExpandScreen
import com.example.finalsproject.ui.screen.UserPlaylistListScreen
import com.example.finalsproject.ui.screen.UserPlaylistScreen
import com.example.finalsproject.ui.viewmodel.HomeScreenViewModel
import com.example.finalsproject.ui.viewmodel.LeaderboardViewModel
import com.example.finalsproject.ui.viewmodel.LikedScreenViewModel
import com.example.finalsproject.ui.viewmodel.MusicQueueViewModel
import com.example.finalsproject.ui.viewmodel.NewPlaylistViewModel
import com.example.finalsproject.ui.viewmodel.PlaylistScreenViewModel
import com.example.finalsproject.ui.viewmodel.ProfileScreenViewModel
import com.example.finalsproject.ui.viewmodel.SearchScreenViewModel
import com.example.finalsproject.ui.viewmodel.SongViewModel
import com.example.finalsproject.ui.viewmodel.UserPlaylistListViewModel
import com.example.finalsproject.ui.viewmodel.UserPlaylistViewModel
import kotlinx.coroutines.flow.map

@Composable
fun HomeNavHost(
    onLogOut: () -> Unit
) {
    val navController = rememberNavController()
    val musicQueueViewModel = viewModel<MusicQueueViewModel>(factory = MusicQueueViewModel.Factory)
    var showMiniPlayer by remember { mutableStateOf(false) }

    val providedSongViewModel = LocalSongViewModel provides
            viewModel(factory = SongViewModel.Factory(musicQueueViewModel))
    val providedNewPlaylistViewModel = LocalNewPlaylistViewModel provides
            viewModel(factory = NewPlaylistViewModel.Factory)

    CompositionLocalProvider(
        providedSongViewModel,
        providedNewPlaylistViewModel
    ) {
        Scaffold(
            bottomBar = {
                val queueNotEmpty by musicQueueViewModel.state
                    .map { it.currentSong != null }
                    .collectAsState(false)
                AnimatedVisibility(
                    visible = showMiniPlayer && queueNotEmpty,
                    enter = CustomTransition.Enter.slideFromBottom,
                    exit = ExitTransition.None
                ) {
                    MiniPlayer(
                        viewModel = musicQueueViewModel,
                        navToSongExpand = {
                            navController.navigate(NavRoutes.SongExpand.route) {
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.navigationBarsPadding()
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = NavRoutes.Home.route,
                enterTransition = { CustomTransition.Enter.slideFromRight },
                exitTransition = { CustomTransition.Exit.slideToLeft },
                popEnterTransition = { CustomTransition.Enter.slideFromLeft },
                popExitTransition = { CustomTransition.Exit.slideToRight },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            ) {
                composable(NavRoutes.Home.route) {
                    showMiniPlayer = true
                    HomeScreen(
                        viewModel = viewModel(factory = HomeScreenViewModel.Factory),
                        navToLeaderboard = {
                            navController.navigate(NavRoutes.Leaderboard.route) {
                                launchSingleTop = true
                            }
                        },
                        navToPlaylist = { id ->
                            navController.navigate(
                                NavRoutes.Playlist.targetRoute(NavRoutes.Playlist.Arg(id = id))
                            ) {
                                launchSingleTop = true
                            }
                        },
                        navToProfile = {
                            navController.navigate(NavRoutes.Profile.route) {
                                launchSingleTop = true
                            }
                        },
                        navToSearch = {
                            navController.navigate(NavRoutes.Search.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(NavRoutes.Search.route) {
                    showMiniPlayer = true
                    SearchScreen(
                        vm = viewModel(factory = SearchScreenViewModel.Factory),
                        musicQueueVm = musicQueueViewModel,
                        navBack = { navController.popBackStack() },
                        navToPlaylist = { id ->
                            navController.navigate(
                                NavRoutes.Playlist.targetRoute(NavRoutes.Playlist.Arg(id = id))
                            ) {
                                launchSingleTop = true
                            }
                        },
                    )
                }

                composable(NavRoutes.Profile.route) {
                    showMiniPlayer = true
                    ProfileScreen(
                        viewModel = viewModel(factory = ProfileScreenViewModel.Factory),
                        navBack = { navController.popBackStack() },
                        navToUserPlaylistList = {
                            navController.navigate(NavRoutes.UserPlaylistList.route)
                        },
                        navToFavoriteList = { navController.navigate(NavRoutes.Liked.route) },
                        navToAbout = { navController.navigate(NavRoutes.About.route) },
                        onLogOut = onLogOut
                    )
                }

                composable(NavRoutes.UserPlaylistList.route) {
                    showMiniPlayer = true
                    UserPlaylistListScreen(
                        viewModel = viewModel(factory = UserPlaylistListViewModel.Factory),
                        navBack = { navController.popBackStack() },
                        navToPlaylist = { id ->
                            val arg = NavRoutes.UserPlaylist.Arg(id)
                            navController.navigate(NavRoutes.UserPlaylist.targetRoute(arg))
                        },
                    )
                }

                composable(NavRoutes.Liked.route) {
                    showMiniPlayer = true
                    LikedScreen(
                        viewModel = viewModel(factory = LikedScreenViewModel.Factory),
                        navBack = { navController.popBackStack() },
                    )
                }

                composable(NavRoutes.About.route) {
                    showMiniPlayer = true
                    AboutScreen(navBack = { navController.popBackStack() })
                }

                composable(NavRoutes.UserPlaylist.routeWithArg) {
                    navController.currentBackStackEntry?.viewModelStore?.clear()
                    showMiniPlayer = true
                    UserPlaylistScreen(
                        viewModel = viewModel(factory = UserPlaylistViewModel.Factory),
                        musicQueueViewModel = musicQueueViewModel,
                        navBack = { navController.popBackStack() }
                    )
                }

                composable(NavRoutes.Leaderboard.route) {
                    showMiniPlayer = true
                    LeaderboardScreen(
                        viewModel = viewModel(factory = LeaderboardViewModel.Factory),
                        musicQueueViewModel = musicQueueViewModel,
                        navBack = { navController.popBackStack() }
                    )
                }

                composable(NavRoutes.Playlist.routeWithArg) {
                    showMiniPlayer = true
                    PlaylistScreen(
                        viewModel = viewModel(factory = PlaylistScreenViewModel.Factory),
                        musicQueueViewModel = musicQueueViewModel,
                        navBack = { navController.popBackStack() },
                    )
                }

                composable(
                    route = NavRoutes.SongExpand.route,
                    enterTransition = { CustomTransition.Enter.slideFromBottom },
                    exitTransition = { CustomTransition.Exit.slideToBottom },
                    popEnterTransition = { CustomTransition.Enter.slideFromBottom },
                    popExitTransition = { CustomTransition.Exit.slideToBottom }
                ) {
                    showMiniPlayer = false
                    SongExpandScreen(
                        viewModel = musicQueueViewModel,
                        navBack = { navController.popBackStack() },
                        navToQueueManager = {
                            navController.navigate(NavRoutes.QueueManager.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(NavRoutes.QueueManager.route) {
                    showMiniPlayer = false
                    QueueManagerScreen(
                        viewModel = musicQueueViewModel,
                        navBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}