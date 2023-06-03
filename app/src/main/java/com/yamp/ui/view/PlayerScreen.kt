package com.yamp.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yamp.R
import com.yamp.media.exoplayer.PlaybackMode
import com.yamp.ui.viewmodel.AudioViewModel

@Composable
fun PlayerScreen(audioViewModel: AudioViewModel = viewModel()) {
	val audio = audioViewModel.currentPlayingAudio.value
	val progress = audioViewModel.currentAudioProgress.value

	if (audio != null)
		Column(
			modifier = Modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.primaryContainer)
				.padding(16.dp), verticalArrangement = Arrangement.Center
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.Center
			) {
				Image(
					painter = painterResource(id = R.drawable.ic_baseline_music_note_24),
					contentDescription = "Album Cover",
					modifier = Modifier
						.fillMaxWidth()
						.aspectRatio(1f)
						.background(MaterialTheme.colorScheme.primary)
						.border(2.dp, MaterialTheme.colorScheme.onBackground)
				)
			}
			Spacer(modifier = Modifier.size(8.dp))
			Column(modifier = Modifier.fillMaxWidth()) {
				Text(
					text = audio.title,
					fontWeight = FontWeight.Bold,
					style = MaterialTheme.typography.headlineMedium,
					overflow = TextOverflow.Ellipsis,
					maxLines = 1
				)
				Text(
					text = if (audio.artist == "_ua") stringResource(id = R.string.unknown_artist) else audio.artist,
					fontWeight = FontWeight.Light,
					style = MaterialTheme.typography.titleSmall,
					overflow = TextOverflow.Ellipsis,
					maxLines = 1
				)
			}
			Spacer(modifier = Modifier.size(32.dp))
			Column(modifier = Modifier.fillMaxWidth()) {
				Slider(
					value = progress,
					onValueChange = { audioViewModel.seekTo(it) },
					valueRange = 0f..100f,
				)
				Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
					Text(text = timeStampToDuration(audioViewModel.currentPlayBackPosition))
					Text(text = timeStampToDuration(audioViewModel.currentDuration))
				}
			}
			Spacer(modifier = Modifier.size(32.dp))
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceAround,
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
						.size(64.dp)
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
			Spacer(modifier = Modifier.size(32.dp))
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.Center,
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(
					when (audioViewModel.playbackMode) {
						PlaybackMode.DEFAULT -> {
							Icons.Filled.Repeat
						}

						PlaybackMode.REPEAT -> {
							Icons.Filled.RepeatOne
						}

						PlaybackMode.RANDOM -> {
							Icons.Filled.Shuffle
						}
					},
					contentDescription = stringResource(id = audioViewModel.playbackMode.strId),
					modifier = Modifier
						.size(32.dp)
						.clickable {
							when (audioViewModel.playbackMode) {
								PlaybackMode.DEFAULT -> {
									audioViewModel.setMode(PlaybackMode.REPEAT)
								}

								PlaybackMode.REPEAT -> {
									audioViewModel.setMode(PlaybackMode.RANDOM)
								}

								PlaybackMode.RANDOM -> {
									audioViewModel.setMode(PlaybackMode.DEFAULT)
								}
							}
						}
				)
			}
		}
}
