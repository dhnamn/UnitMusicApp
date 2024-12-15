package com.example.finalsproject.ui.navhost

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finalsproject.ui.viewmodel.MainNavHostViewModel

private const val TAG = "MainNavHost"

@Composable
fun MainNavHost(
    viewModel: MainNavHostViewModel,
    exit: () -> Unit
) {
    val navController = rememberNavController()
    var startRoute by remember { mutableStateOf<NavRoute?>(null) }
    var isFirstEnter by remember { mutableStateOf(true) }

    LaunchedEffect(startRoute) {
        if (startRoute != null) {
            return@LaunchedEffect
        }
        startRoute = if (viewModel.hasToken()) {
            NavRoutes.HomeNavHost
        } else if (viewModel.hasAccount()) {
            NavRoutes.Login
        } else {
            NavRoutes.Register
        }
        Log.d(TAG, "Start route: $startRoute")
    }

    if (startRoute == null) {
        Log.d(TAG, "Entering main loading screen")
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                CircularProgressIndicator()
            }
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.MainTransition.route,
        enterTransition = { CustomTransition.Enter.slideFromRight },
        exitTransition = { CustomTransition.Exit.slideToLeft },
        popEnterTransition = { CustomTransition.Enter.slideFromLeft },
        popExitTransition = { CustomTransition.Exit.slideToRight }
    ) {
        composable(NavRoutes.MainTransition.route) {
            Log.d(TAG, "Entering transition screen")

            if (!isFirstEnter) {
                exit()
            }
            if (startRoute == NavRoutes.HomeNavHost) {
                navController.navigate(NavRoutes.HomeNavHost.route)
            } else {
                navController.navigate(NavRoutes.AuthNavHost.route)
            }
            SideEffect {
                isFirstEnter = false
            }
        }

        composable(NavRoutes.AuthNavHost.route) {
            AuthNavHost(
                startRoute = startRoute!!,
                navToHome = {
                    navController.navigate(NavRoutes.HomeNavHost.route) {
                        popUpTo(NavRoutes.MainTransition.route)
                    }
                }
            )
        }

        composable(NavRoutes.HomeNavHost.route) {
            HomeNavHost(
                onLogOut = {
                    navController.popBackStack()
                    startRoute = null
                    isFirstEnter = true
                }
            )
        }
    }
}