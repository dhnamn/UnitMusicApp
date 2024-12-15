package com.example.finalsproject.ui.navhost

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finalsproject.ui.screen.ConfirmationScreen
import com.example.finalsproject.ui.viewmodel.ConfirmationScreenViewModel
import com.example.finalsproject.ui.screen.LoginScreen
import com.example.finalsproject.ui.viewmodel.LoginScreenViewModel
import com.example.finalsproject.ui.screen.RegisterScreen
import com.example.finalsproject.ui.viewmodel.RegisterScreenViewModel

@Composable
fun AuthNavHost(
    startRoute: NavRoute,
    navToHome: () -> Unit,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startRoute.route,
        enterTransition = { CustomTransition.Enter.slideFromRight },
        exitTransition = { CustomTransition.Exit.slideToLeft },
        popEnterTransition = { CustomTransition.Enter.slideFromLeft },
        popExitTransition = { CustomTransition.Exit.slideToRight }
    ) {
        composable(NavRoutes.Register.route) {
            RegisterScreen(
                viewModel = viewModel(factory = RegisterScreenViewModel.Factory),
                navToConfirmation = { arg ->
                    navController.navigate(NavRoutes.Confirmation.targetRoute(arg)) {
                        launchSingleTop = true
                    }
                },
                navToLogin = {
                    if (
                        !navController.popBackStack(
                            route = NavRoutes.Login.route,
                            inclusive = false
                        )
                    ) {
                        navController.navigate(NavRoutes.Login.route)
                    }
                }
            )
        }

        composable(NavRoutes.Login.route) {
            LoginScreen(
                viewModel = viewModel(factory = LoginScreenViewModel.Factory),
                navToRegister = {
                    if (
                        !navController.popBackStack(
                            route = NavRoutes.Register.route,
                            inclusive = false
                        )
                    ) {
                        navController.navigate(NavRoutes.Register.route)
                    }
                },
                navToConfirmation = { arg ->
                    navController.navigate(NavRoutes.Confirmation.targetRoute(arg)) {
                        launchSingleTop = true
                    }
                },
                navToHome = navToHome
            )
        }

        composable(NavRoutes.Confirmation.routeWithArg) {
            ConfirmationScreen(
                viewModel = viewModel(factory = ConfirmationScreenViewModel.Factory),
                navToLogin = {
                    if (
                        !navController.popBackStack(
                            route = NavRoutes.Login.route,
                            inclusive = false
                        )
                    ) {
                        navController.navigate(NavRoutes.Login.route)
                    }
                }
            )
        }
    }
}