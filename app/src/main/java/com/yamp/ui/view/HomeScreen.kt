package com.yamp.ui.audio

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.yamp.R
import com.yamp.data.model.Audio
import com.yamp.ui.view.NavigationScreen
import com.yamp.ui.viewmodel.AudioViewModel
import kotlin.math.floor

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
	navController: NavController,
	audioViewModel: AudioViewModel = viewModel()
) {
	val permissionState = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
	val lifecycleOwner = LocalLifecycleOwner.current

	val currentAudio = audioViewModel.currentPlayingAudio.value

	DisposableEffect(key1 = lifecycleOwner) {
		val observer = LifecycleEventObserver { _, event ->
			if (event == Lifecycle.Event.ON_RESUME) permissionState.launchPermissionRequest()
		}
		lifecycleOwner.lifecycle.addObserver(observer)
		onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
	}

	Column(modifier = Modifier.fillMaxWidth()) {
		if (permissionState.hasPermission) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.background(MaterialTheme.colorScheme.primaryContainer)
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp),
					horizontalArrangement = Arrangement.Center,
					verticalAlignment = Alignment.CenterVertically
				) {
					OutlinedTextField(
						value = audioViewModel.query,
						onValueChange = { audioViewModel.setQuery(it) },
						modifier = Modifier.weight(1F),
						maxLines = 1,
						singleLine = true,
						shape = RoundedCornerShape(0.dp),
						placeholder = { Text(text = stringResource(id = R.string.search)) },
						leadingIcon = { Icon(Icons.Filled.Search, "Search") },
						trailingIcon = {
							if (audioViewModel.query.isNotEmpty())
								IconButton(onClick = { audioViewModel.setQuery("") }) {
									Icon(Icons.Filled.Close, "Empty")
								}
						}
					)
					Spacer(modifier = Modifier.size(8.dp))
					IconButton(onClick = { navController.navigate(NavigationScreen.Settings.route) }) {
						Icon(Icons.Filled.Settings, contentDescription = "Settings")
					}
				}
			}

			LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				items(audioViewModel.audioList.filter {
					it.displayName.lowercase().contains(audioViewModel.query.lowercase())
				}) { audio: Audio ->
					AudioItem(
						navController = navController,
						audio = audio,
						onItemClick = { audioViewModel.playAudio(audio, true) },
						active = if (currentAudio == null) false else audio.id == currentAudio.id
					)
				}
			}
		} else {
			Column(
				modifier = Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(text = stringResource(id = R.string.missing_permissions))
			}
		}
	}
}

@Composable
fun AudioItem(
	navController: NavController,
	audio: Audio,
	onItemClick: (id: Long) -> Unit,
	active: Boolean
) {
	Row(
		Modifier
			.fillMaxWidth()
			.clickable {
				onItemClick.invoke(audio.id)
				navController.navigate(NavigationScreen.Player.route)
			}, verticalAlignment = Alignment.CenterVertically
	) {
		Column(
			modifier = Modifier
				.weight(1f)
				.padding(horizontal = 16.dp, vertical = 8.dp)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Image(
					painter = painterResource(id = R.drawable.ic_baseline_music_note_24),
					contentDescription = "Album Cover",
					modifier = Modifier
						.size(48.dp)
						.aspectRatio(1f)
						.background(MaterialTheme.colorScheme.primary)
				)
				Column {
					Text(
						text = audio.displayName,
						fontWeight = FontWeight.SemiBold,
						style = MaterialTheme.typography.headlineSmall,
						overflow = TextOverflow.Clip,
						color = if (active) MaterialTheme.colorScheme.inversePrimary else Color.Unspecified,
						maxLines = 1
					)
					Text(
						text = if (audio.artist == "_ua") stringResource(id = R.string.unknown_artist) else audio.artist,
						fontWeight = FontWeight.Light,
						style = MaterialTheme.typography.bodySmall,
						maxLines = 1,
						overflow = TextOverflow.Clip,
						color = if (active) MaterialTheme.colorScheme.inversePrimary else Color.Unspecified,
					)
				}
			}
		}
		Text(
			text = timeStampToDuration(audio.duration.toLong()),
			color = if (active) MaterialTheme.colorScheme.inversePrimary else Color.Unspecified,
		)
		Spacer(modifier = Modifier.size(16.dp))
	}
}

fun timeStampToDuration(position: Long): String {
	val totalSeconds = floor(position / 1E3).toInt()
	val minutes = totalSeconds / 60
	val remainingSeconds = totalSeconds - (minutes * 60)

	return if (position < 0) "--:--"
	else "%d:%02d".format(minutes, remainingSeconds)
}