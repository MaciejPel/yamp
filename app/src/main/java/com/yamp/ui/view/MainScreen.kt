package com.yamp.ui.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.yamp.R
import com.yamp.data.model.Audio
import com.yamp.ui.viewmodel.AudioViewModel
import com.yamp.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(settingsViewModel: SettingsViewModel = viewModel(), audioViewModel: AudioViewModel) {
	val navController = rememberAnimatedNavController()
	val navigationViewModel: NavigationViewModel = viewModel()
	val navigationUiState by navigationViewModel.uiState.collectAsState()

	val currentPlayingAudio = audioViewModel.currentPlayingAudio.value

	Scaffold(
		containerColor = MaterialTheme.colorScheme.background,
		bottomBar = {
			if (navigationUiState.route != NavigationScreen.Player.route)
				currentPlayingAudio?.let { currentPlayingAudio ->
					BottomAppBar(contentPadding = PaddingValues(0.dp)) {
						BottomBarPlayer(
							navController = navController,
							audio = currentPlayingAudio,
							audioViewModel = audioViewModel
						)
					}
				}
		}) { innerPadding ->
		NavGraph(
			navController = navController,
			navigationViewModel = navigationViewModel,
			innerPadding = innerPadding,
			audioViewModel = audioViewModel,
			settingsViewModel = settingsViewModel,
		)
	}
}

@Composable
fun BottomBarPlayer(
	navController: NavController,
	audio: Audio,
	audioViewModel: AudioViewModel
) {
	Row(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.primaryContainer)
			.clickable { navController.navigate(NavigationScreen.Player.route) }
			.padding(16.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.Top
	) {
		Row(
			horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.widthIn(0.dp, 244.dp)
		) {
			Image(
				painter = painterResource(id = R.drawable.ic_baseline_music_note_24),
				contentDescription = "Album Cover",
				modifier = Modifier
					.size(48.dp)
					.aspectRatio(1f)
					.background(MaterialTheme.colorScheme.primary)
			)
			Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
				Text(
					text = audio.title,
					fontWeight = FontWeight.Bold,
					style = MaterialTheme.typography.titleMedium,
					overflow = TextOverflow.Clip,
					maxLines = 1,
				)
				Text(
					text = if (audio.artist == "_ua") stringResource(id = R.string.unknown_artist) else audio.artist,
					fontWeight = FontWeight.Light,
					style = MaterialTheme.typography.bodySmall,
					overflow = TextOverflow.Clip,
					maxLines = 1
				)
			}
		}
		Row(
			modifier = Modifier.height(48.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Icon(
				Icons.Filled.SkipPrevious,
				contentDescription = "Previous",
				modifier = Modifier
					.size(32.dp)
					.clickable { audioViewModel.skipToPrevious() }
			)
			Icon(
				if (audioViewModel.isAudioPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
				contentDescription = if (audioViewModel.isAudioPlaying) "Pause" else "Play",
				modifier = Modifier
					.size(32.dp)
					.clickable { audioViewModel.playAudio(audio) }
			)
			Icon(
				Icons.Filled.SkipNext,
				contentDescription = "Next",
				modifier = Modifier
					.size(32.dp)
					.clickable { audioViewModel.skipToNext() }
			)
		}
	}
}
