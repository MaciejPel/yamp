package com.yamp.ui.view

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.yamp.ui.audio.HomeScreen
import com.yamp.ui.viewmodel.AudioViewModel
import com.yamp.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(
	navController: NavHostController,
	innerPadding: PaddingValues,
	navigationViewModel: NavigationViewModel = viewModel(),
	audioViewModel: AudioViewModel = viewModel(),
	settingsViewModel: SettingsViewModel = viewModel(),
) {
	AnimatedNavHost(
		modifier = Modifier.padding(innerPadding),
		navController = navController,
		startDestination = NavigationScreen.Home.route
	) {
		composable(
			route = NavigationScreen.Home.route,
			enterTransition = {
				slideIntoContainer(
					AnimatedContentTransitionScope.SlideDirection.Right,
					animationSpec = tween(400)
				)
			},
			exitTransition = {
				slideOutOfContainer(
					AnimatedContentTransitionScope.SlideDirection.Left,
					animationSpec = tween(400)
				)
			},
		) {
			navigationViewModel.setNavigation(NavigationScreen.Home.title, NavigationScreen.Home.route)
			HomeScreen(navController, audioViewModel)
		}
		composable(
			route = NavigationScreen.Player.route,
			enterTransition = {
				slideIntoContainer(
					AnimatedContentTransitionScope.SlideDirection.Left,
					animationSpec = tween(400)
				)
			},
			exitTransition = {
				slideOutOfContainer(
					AnimatedContentTransitionScope.SlideDirection.Right,
					animationSpec = tween(400)
				)
			}
		) {
			navigationViewModel.setNavigation(
				NavigationScreen.Player.title,
				NavigationScreen.Player.route
			)
			PlayerScreen(audioViewModel)
		}
		composable(
			route = NavigationScreen.Settings.route,
			enterTransition = {
				slideIntoContainer(
					AnimatedContentTransitionScope.SlideDirection.Up,
					animationSpec = tween(400)
				)
			},
			exitTransition = {
				slideOutOfContainer(
					AnimatedContentTransitionScope.SlideDirection.Down,
					animationSpec = tween(400)
				)
			}
		) {
			navigationViewModel.setNavigation(
				NavigationScreen.Settings.title,
				NavigationScreen.Settings.route
			)
			SettingsScreen(settingsViewModel)
		}
	}
}