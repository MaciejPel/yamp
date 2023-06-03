package com.yamp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class PreferencesRepository(
	private val dataStore: DataStore<Preferences>
) {
	val isDarkTheme: Flow<Int> = dataStore.data
		.catch {
			if (it is IOException) {
				emit(emptyPreferences())
			} else {
				throw it
			}
		}
		.map { preferences ->
			preferences[IS_DARK_THEME] ?: 0
		}

	private companion object {
		val IS_DARK_THEME = intPreferencesKey("theme")
		const val TAG = "PreferencesRepo"
	}

	suspend fun saveThemePreference(isDarkTheme: Int) {
		dataStore.edit { preferences ->
			preferences[IS_DARK_THEME] = isDarkTheme
		}
	}
}