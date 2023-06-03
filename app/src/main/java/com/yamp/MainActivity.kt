package com.yamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.yamp.ui.theme.YampTheme
import com.yamp.ui.view.MainScreen
import com.yamp.ui.viewmodel.AudioViewModel
import com.yamp.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		WindowCompat.setDecorFitsSystemWindows(window, false)
		setContent {
			val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
			val settingsUiState by settingsViewModel.uiState.collectAsState()
			val audioViewModel: AudioViewModel = viewModel()

			YampTheme(darkTheme = settingsUiState.theme.valueBool) {
				MainScreen(settingsViewModel, audioViewModel)
			}
		}
	}
}




