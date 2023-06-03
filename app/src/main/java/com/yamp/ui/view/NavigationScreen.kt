package com.yamp.ui.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationScreen(
	val route: String,
	val title: String,
	val icon: ImageVector
) {
	object Home :
		NavigationScreen("home", "Home", Icons.Filled.Home)

	object Player :
		NavigationScreen("player", "Player", Icons.Filled.Home)

	object Settings :
		NavigationScreen("settings", "Settings", Icons.Filled.Settings)
}