package com.yamp.ui.viewmodel

import android.support.v4.media.MediaBrowserCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yamp.data.model.Audio
import com.yamp.data.repository.AudioRepository
import com.yamp.media.constants.K
import com.yamp.media.exoplayer.MediaPlayerServiceConnection
import com.yamp.media.exoplayer.PlaybackMode
import com.yamp.media.exoplayer.currentPosition
import com.yamp.media.exoplayer.isPlaying
import com.yamp.media.service.MediaPlayerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
	private val repository: AudioRepository,
	serviceConnection: MediaPlayerServiceConnection
) : ViewModel() {
	var audioList = mutableStateListOf<Audio>()
	val currentPlayingAudio = serviceConnection.currentPlayingAudio
	private val isConnected = serviceConnection.isConnected
	lateinit var rootMediaId: String
	var currentPlayBackPosition by mutableStateOf(0L)
	private var updatePosition = true
	private val playbackState = serviceConnection.playBackState

	val isAudioPlaying: Boolean
		get() = playbackState.value?.isPlaying == true

	private var _query: String by mutableStateOf("")
	val query: String
		get() = _query

	private var _playbackMode: PlaybackMode by mutableStateOf(PlaybackMode.DEFAULT)
	val playbackMode: PlaybackMode
		get() = _playbackMode

	fun setQuery(query: String) {
		_query = query
	}

	private val subscriptionCallback = object
		: MediaBrowserCompat.SubscriptionCallback() {
		override fun onChildrenLoaded(
			parentId: String,
			children: MutableList<MediaBrowserCompat.MediaItem>
		) {
			super.onChildrenLoaded(parentId, children)
		}
	}

	private val serviceConnection = serviceConnection.also {
		updatePlayBack()
	}

	val currentDuration: Long
		get() = MediaPlayerService.currentDuration

	var currentAudioProgress = mutableStateOf(0f)

	init {
		viewModelScope.launch {
			audioList += getAndFormatAudioData()
			isConnected.collect {
				if (it) {
					rootMediaId = serviceConnection.rootMediaId
					serviceConnection.playBackState.value?.apply {
						currentPlayBackPosition = position
					}
					serviceConnection.subscribe(rootMediaId, subscriptionCallback)
				}
			}
		}
	}

	private suspend fun getAndFormatAudioData(): List<Audio> {
		return repository.getAudioData().map {
			val displayName = it.displayName.substringBefore(".")
			val artist = if (it.artist.contains("<unknown>"))
				"_ua" else it.artist
			it.copy(
				displayName = displayName,
				artist = artist
			)
		}
	}

	fun playAudio(currentAudio: Audio, fromList: Boolean = false) {
		setMode(_playbackMode)
		serviceConnection.playAudio(audioList)
		if (currentAudio.id == currentPlayingAudio.value?.id) {
			if (isAudioPlaying && !fromList) {
				serviceConnection.transportControl.pause()
			} else {
				serviceConnection.transportControl.play()
			}
		} else {
			serviceConnection.transportControl.playFromMediaId(currentAudio.id.toString(), null)
		}
	}

	fun pauseAudio() {
		serviceConnection.transportControl.pause()
	}

	fun stopPlayback() {
		serviceConnection.transportControl.stop()
	}

	fun fastForward() {
		serviceConnection.fastForward()
	}

	fun rewind() {
		serviceConnection.rewind()
	}

	fun skipToNext() {
		if (_playbackMode == PlaybackMode.REPEAT)
			_playbackMode = PlaybackMode.DEFAULT
		serviceConnection.skipToNext()
	}

	fun skipToPrevious() {
		if (_playbackMode == PlaybackMode.REPEAT)
			_playbackMode = PlaybackMode.DEFAULT
		serviceConnection.skipToPrevious()
	}

	fun setMode(mode: PlaybackMode) {
		_playbackMode = mode
		serviceConnection.setMode(mode)
	}

	fun seekTo(value: Float) {
		serviceConnection.transportControl.seekTo(
			(currentDuration * value / 100f).toLong()
		)
	}

	private fun updatePlayBack() {
		viewModelScope.launch {
			val position = playbackState.value?.currentPosition ?: 0

			if (currentPlayBackPosition != position) {
				currentPlayBackPosition = position
			}

			if (currentDuration > 0) {
				currentAudioProgress.value =
					(currentPlayBackPosition.toFloat() / currentDuration.toFloat() * 100f)
			}

			delay(K.PLAYBACK_UPDATE_INTERVAL)
			if (updatePosition) {
				updatePlayBack()
			}
		}
	}

	override fun onCleared() {
		super.onCleared()
		serviceConnection.unSubscribe(
			K.MEDIA_ROOT_ID,
			object : MediaBrowserCompat.SubscriptionCallback() {}
		)
		updatePosition = false
	}
}